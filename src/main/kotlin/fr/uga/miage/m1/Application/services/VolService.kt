package fr.uga.miage.m1.Application.services

import fr.uga.miage.m1.Application.aeroportCentrale.CentralAirport
import fr.uga.miage.m1.Application.aeroportCentrale.FonctionAeroportPartenaire
import fr.uga.miage.m1.Domain.updateRequest.UpdateVolRequest
import fr.uga.miage.m1.Infrastucture.adapters.AvionAdapter
import fr.uga.miage.m1.Infrastucture.adapters.PisteAdapter
import fr.uga.miage.m1.Infrastucture.adapters.PlannningPistesAdapter
import fr.uga.miage.m1.Infrastucture.adapters.VolAdapter
import fr.uga.miage.m1.Domain.enums.Etat
import fr.uga.miage.m1.Domain.enums.Statut
import fr.uga.miage.m1.Application.mapperDomain.VolMapperDomain
import fr.uga.miage.m1.Infrastucture.mappers.AvionMapper
import fr.uga.miage.m1.Infrastucture.mappers.PisteMapper
import fr.uga.miage.m1.Infrastucture.mappers.VolMapper
import fr.uga.miage.m1.Infrastucture.models.AvionEntity
import fr.uga.miage.m1.Infrastucture.models.PisteEntity
import fr.uga.miage.m1.Application.requestDTO.HistoriqueRequestDTO
import fr.uga.miage.m1.Application.requestDTO.VolRequestDTO
import fr.uga.miage.m1.Application.responseDTO.VolExterneBJAResponseDTO
import fr.uga.miage.m1.Application.responseDTO.VolResponseDTO
import fr.uga.miage.m1.Domain.domains.VolDomain
import fr.uga.miage.m1.Domain.enums.TypeVol
import fr.uga.miage.m1.Infrastucture.mappers.PlanningPistesMapper
import fr.uga.miage.m1.models.AeroportEntity
import fr.uga.miage.m1.mappers.AeroportMapper
import fr.uga.miage.m1.adapters.AeroportAdapter
import fr.uga.miage.m1.factory.VolFactory
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.time.LocalDateTime
import kotlin.math.abs

@Service
class VolService(
    private val avionAdapter: AvionAdapter,
    private val volAdapter: VolAdapter,
    private val pisteAdapter: PisteAdapter,
    private val aeroportAdapter: AeroportAdapter,
    private val historiqueService: HistoriqueService,
    private val plannningPistesAdapter: PlannningPistesAdapter,
    private val volMapper: VolMapper,
    private val volMapperDomain: VolMapperDomain,
    private val avionMapper: AvionMapper,
    private val pisteMapper: PisteMapper,
    private val aeroportMapper: AeroportMapper,
    private val volFactory: VolFactory,
    private val planningPistesMapper: PlanningPistesMapper,
    private val restTemplate: RestTemplate,
    @Value("\${external.vols.bja.departs-url}")
    private val externalVolsDepartUrl: String,
) {

    fun getVolExterne(): Collection<VolResponseDTO> {
        val response = restTemplate.exchange(
            externalVolsDepartUrl,
            HttpMethod.GET,
            null,
            object : ParameterizedTypeReference<List<VolExterneBJAResponseDTO>>() {}
        )

        val volsExternes = response.body ?: emptyList()

        val volsExternesVersCentral = volsExternes.filter {
            it.destination == CentralAirport.VILLE
        }

        return volsExternesVersCentral.map { ext ->
            VolResponseDTO(
                id = null,
                numeroVol = ext.numeroVol,
                compagnie = "",
                origineId = null,
                destinationId = null,
                dateDepart = ext.heureDepart,
                dateArrivee = ext.heureArrivee,
                statut = FonctionAeroportPartenaire.mapStatutExterne(ext.statut),
                typeVol = TypeVol.NORMAL,
                avionId = null,
                pisteDecollageId = null,
                pisteAtterissageId = null
            )
        }
    }

    fun getAllVols(): Collection<VolResponseDTO> {
        val volsLocaux = volAdapter.getAllVols().map { volMapperDomain.domainToResponse(it) }
        val volsExternes = getVolExterne()
        return volsLocaux + volsExternes
    }

    fun getVolById(id: Long): VolResponseDTO =
        volAdapter.getVolById(id)
            .let { volMapperDomain.domainToResponse(it) }

    @Transactional
    fun createVol(volRequest: VolRequestDTO): VolResponseDTO {
        val volDomain = volFactory.create(volRequest)
        return volMapperDomain.domainToResponse(volDomain)
    }

    @Transactional
    fun deleteVolvById(id: Long) {
        val vol = volAdapter.getVolById(id)

        vol.planningPistesEntity?.let { planning ->
            planning.volsDepart.removeIf { it.id == vol.id }
            planning.volsArrivee.removeIf { it.id == vol.id }
            val planningDomain = planningPistesMapper.entityToDomain(planning)
            plannningPistesAdapter.savePlanningPistes(planningDomain)
        }

        volAdapter.deleteVolById(id)
    }

    fun searchByNumVol(numVol: String): VolResponseDTO =
        volAdapter.searchByNumVol(numVol)
            ?.let { volMapperDomain.domainToResponse(it) }
            ?: throw NoSuchElementException("Vol $numVol introuvable")

    fun listesVolParStatut(statut: Statut): Collection<VolResponseDTO> {
        val vols = volAdapter.listesVolParStatut(statut)
        if (vols.isEmpty()) {
            throw IllegalArgumentException("Aucun vol trouvé pour le statut $statut")
        }
        return vols.map { volMapperDomain.domainToResponse(it) }
    }

    fun getStatutById(id: Long): Statut? =
        volAdapter.getVolById(id).statut

    @Transactional
    fun updateVol(id: Long, updateVol: UpdateVolRequest): VolResponseDTO {
        val volDomain = volAdapter.getVolById(id)
        val ancienStatut = volDomain.statut
        val volEntity = volMapper.domainToEntity(volDomain)

        val updateOrigine: AeroportEntity? = null

        val updateDestination: AeroportEntity? = updateVol.destinationId?.let { destinationId ->
            val aeroportDomain = aeroportAdapter.getAeroportById(destinationId)
            aeroportMapper.domainToEntity(aeroportDomain)
        }

        val updateAvion: AvionEntity? = updateVol.avionId?.let { avionId ->
            val avionDomain = avionAdapter.getAvionById(avionId)
            avionMapper.domainToEntity(avionDomain)
        }

        val updatePisteDecollage: PisteEntity? =
            updateVol.pisteDecollageId?.let { pisteId ->
                verifierDisponibilitePisteDecollage(pisteId, id, updateVol.dateDepart)
            }

        val updatePisteAtterissage: PisteEntity? =
            updateVol.pisteAtterissageId?.let { pisteId ->
                verifierDisponibilitePisteAtterissage(pisteId, id, updateVol.dateDepart)
            }

        mettreAJourEtatPistesSelonStatut(volDomain.statut, updatePisteDecollage, updatePisteAtterissage)
        validerOrdreDates(updateVol.dateDepart, updateVol.dateArrivee)

        volMapper.toUpdate(
            updateVol,
            volEntity,
            updateAvion,
            updatePisteDecollage,
            updatePisteAtterissage,
            updateOrigine,
            updateDestination
        )

        val saveVol = volAdapter.saveVol(volEntity)
            ?: throw IllegalStateException("Erreur lors de la sauvegarde du vol")

        createHistoriqueIfStatutChanged(saveVol, ancienStatut)

        return volMapperDomain.domainToResponse(saveVol)
    }

    private fun verifierDisponibilitePisteDecollage(
        pisteId: Long,
        volId: Long,
        dateDepart: LocalDateTime
    ): PisteEntity {
        val pisteDomain = pisteAdapter.getPisteById(pisteId)
        val pisteEntity = pisteMapper.domainToEntity(pisteDomain)
        val vols = volAdapter.getAllVols()
            .filter { it.id != volId && it.pisteDecollage?.id == pisteEntity.id && it.statut != Statut.ANNULE }

        verifierAbsenceConflitHoraire(vols, dateDepart) {
            " la mise à jour de la piste de décollage $pisteId ne peut pas être faite "
        }
        return pisteEntity
    }

    private fun verifierDisponibilitePisteAtterissage(
        pisteId: Long,
        volId: Long,
        dateDepart: LocalDateTime
    ): PisteEntity {
        val pisteDomain = pisteAdapter.getPisteById(pisteId)
        val pisteEntity = pisteMapper.domainToEntity(pisteDomain)
        val vols = volAdapter.getAllVols()
            .filter { it.id != volId && it.pisteAtterissage?.id == pisteEntity.id && it.statut != Statut.ANNULE }

        verifierAbsenceConflitHoraire(vols, dateDepart) {
            " la mise à jour de la piste d'atterissage $pisteId ne peut pas être faite "
        }
        return pisteEntity
    }

    private fun verifierAbsenceConflitHoraire(
        vols: Collection<VolDomain>,
        nouvelleDateDepart: LocalDateTime,
        errorMessage: () -> String
    ) {
        val conflit = vols.any { vol -> isDateConflict(nouvelleDateDepart, vol.dateDepart) }
        if (conflit) {
            throw IllegalArgumentException(errorMessage())
        }
    }

    private fun isDateConflict(date1: LocalDateTime, date2: LocalDateTime): Boolean {
        if (date1.toLocalDate() != date2.toLocalDate()) return false
        if (date1.hour != date2.hour) return false
        return abs(date1.minute - date2.minute) <= 5
    }

    private fun mettreAJourEtatPistesSelonStatut(
        statut: Statut,
        pisteDecollage: PisteEntity?,
        pisteAtterissage: PisteEntity?
    ) {
        if (statut == Statut.EMBARQUEMENT || statut == Statut.ENVOL) {
            pisteDecollage?.let { if (it.etat != Etat.MAINTENANCE) it.etat = Etat.OCCUPE }
            pisteAtterissage?.let { if (it.etat != Etat.MAINTENANCE) it.etat = Etat.OCCUPE }
        } else if (
            statut == Statut.DECOLLE ||
            statut == Statut.ARRIVE ||
            statut == Statut.ANNULE
        ) {
            pisteDecollage?.let { if (it.etat != Etat.MAINTENANCE) it.etat = Etat.LIBRE }
            pisteAtterissage?.let { if (it.etat != Etat.MAINTENANCE) it.etat = Etat.LIBRE }
        }
    }

    private fun validerOrdreDates(dateDepart: LocalDateTime, dateArrivee: LocalDateTime) {
        if (dateArrivee.isBefore(dateDepart)) {
            throw IllegalArgumentException("Attention la date de depart doit avant la date d'arrivée")
        }
    }

    private fun createHistoriqueIfStatutChanged(saveVol: VolDomain, ancienStatut: Statut) {
        if (saveVol.statut != ancienStatut) {
            val request = HistoriqueRequestDTO(
                idVol = saveVol.id,
                statut = saveVol.statut
            )
            historiqueService.createHistorique(request)
        }
    }
}
