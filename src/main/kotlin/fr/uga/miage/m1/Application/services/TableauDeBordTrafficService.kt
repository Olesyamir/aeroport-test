package fr.uga.miage.m1.services

import fr.uga.miage.m1.Application.aeroportCentrale.BjaAirport
import fr.uga.miage.m1.Application.mapperDomain.HistoriqueMapperDomain
import fr.uga.miage.m1.Application.mapperDomain.PlanningPistesMapperDomain
import fr.uga.miage.m1.Application.responseDTO.HistoriqueResponseDTO
import fr.uga.miage.m1.Application.responseDTO.PlanningPistesResponseDTO
import fr.uga.miage.m1.Application.responseDTO.VolExterneBJAResponseDTO
import fr.uga.miage.m1.Domain.domains.HangarDomain
import fr.uga.miage.m1.Domain.domains.PisteDomain
import fr.uga.miage.m1.Domain.domains.VolDomain
import fr.uga.miage.m1.Domain.enums.Etat
import fr.uga.miage.m1.Domain.enums.Statut
import fr.uga.miage.m1.Infrastucture.adapters.ExternalVolBjaAdapter
import fr.uga.miage.m1.Infrastucture.adapters.HangarAdapter
import fr.uga.miage.m1.Infrastucture.adapters.HistoriqueAdapter
import fr.uga.miage.m1.Infrastucture.adapters.PisteAdapter
import fr.uga.miage.m1.Infrastucture.adapters.PlannningPistesAdapter
import fr.uga.miage.m1.Infrastucture.adapters.VolAdapter
import fr.uga.miage.m1.adapters.AeroportAdapter
import fr.uga.miage.m1.reponses.StatistiquesTrafficDTO
import fr.uga.miage.m1.reponses.TableauDeBordTrafficDTO
import fr.uga.miage.m1.reponses.VolTrafficDTO
import org.springframework.stereotype.Service

@Service
class TableauDeBordTrafficService(
    private val aeroportAdapter: AeroportAdapter,
    private val volAdapter: VolAdapter,
    private val pisteAdapter: PisteAdapter,
    private val hangarAdapter: HangarAdapter,
    private val historiqueAdapter: HistoriqueAdapter,
    private val planningPistesAdapter: PlannningPistesAdapter,
    private val historiqueMapperDomain: HistoriqueMapperDomain,
    private val planningPistesMapperDomain: PlanningPistesMapperDomain,
    private val externalVolBjaAdapter: ExternalVolBjaAdapter
) {

    /**
     * Tableau de bord global d'un aéroport Anways.
     * On utilise uniquement les vols internes (base locale).
     */
    fun getTableauDeBord(aeroportId: Long): TableauDeBordTrafficDTO {
        val aeroport = aeroportAdapter.getAeroportById(aeroportId)

        val tousLesVols = volAdapter.getAllVols()
        val volsDepartInternes = tousLesVols.filter { it.origine?.id == aeroportId }
        val volsArriveeInternes = tousLesVols.filter { it.destination?.id == aeroportId }

        val toutesLesPistes = pisteAdapter.getAllPiste()
        val tousLesHangars = hangarAdapter.getAllHangars()

        val pistesDisponibles = toutesLesPistes.count { it.etat == Etat.LIBRE }
        val pistesOccupees = toutesLesPistes.count { it.etat == Etat.OCCUPE }
        val pistesEnMaintenance = toutesLesPistes.count { it.etat == Etat.MAINTENANCE }

        val hangarsDisponibles = tousLesHangars.count { it.avionEntities.size < it.capacite }
        val hangarsOccupes = tousLesHangars.count { it.avionEntities.size >= it.capacite }

        val volsDepartInternesDTO = volsDepartInternes.map { convertirEnVolTrafficDTO(it) }
        val volsArriveeInternesDTO = volsArriveeInternes.map { convertirEnVolTrafficDTO(it) }

        var volsDepartDTO: List<VolTrafficDTO> = volsDepartInternesDTO
        var volsArriveeDTO: List<VolTrafficDTO> = volsArriveeInternesDTO

        if (aeroport.codeIATA == BjaAirport.CODE_IATA) {
            val volsDepartExternes = externalVolBjaAdapter.getDeparts().map { convertirVolExterneEnTrafficDTO(it) }
            val volsArriveeExternes = externalVolBjaAdapter.getArrivees().map { convertirVolExterneEnTrafficDTO(it) }

            volsDepartDTO = volsDepartInternesDTO + volsDepartExternes
            volsArriveeDTO = volsArriveeInternesDTO + volsArriveeExternes
        }

        val statistiques = calculerStatistiques(volsDepartDTO, volsArriveeDTO, toutesLesPistes, tousLesHangars)

        return TableauDeBordTrafficDTO(
            aeroportId = aeroport.id!!,
            aeroportNom = aeroport.nom,
            aeroportCodeIATA = aeroport.codeIATA,
            statistiques = statistiques,
            volsDepart = volsDepartDTO,
            volsArrivee = volsArriveeDTO,
            pistesDisponibles = pistesDisponibles,
            pistesOccupees = pistesOccupees,
            pistesEnMaintenance = pistesEnMaintenance,
            hangarsDisponibles = hangarsDisponibles,
            hangarsOccupes = hangarsOccupes
        )
    }

    fun getVolsDepart(aeroportId: Long): Collection<VolTrafficDTO> {
        val tousLesVols = volAdapter.getAllVols()
        return tousLesVols
            .filter { it.origine?.id == aeroportId }
            .map { convertirEnVolTrafficDTO(it) }
    }

    fun getVolsArrivee(aeroportId: Long): Collection<VolTrafficDTO> {
        val tousLesVols = volAdapter.getAllVols()
        return tousLesVols
            .filter { it.destination?.id == aeroportId }
            .map { convertirEnVolTrafficDTO(it) }
    }

    fun getVolsParStatut(aeroportId: Long, statut: Statut): Collection<VolTrafficDTO> {
        val tousLesVols = volAdapter.getAllVols()
        return tousLesVols
            .filter {
                (it.origine?.id == aeroportId || it.destination?.id == aeroportId) &&
                        it.statut == statut
            }
            .map { convertirEnVolTrafficDTO(it) }
    }

    fun getVolsEnCours(aeroportId: Long): Collection<VolTrafficDTO> {
        val tousLesVols = volAdapter.getAllVols()
        return tousLesVols
            .filter {
                (it.origine?.id == aeroportId || it.destination?.id == aeroportId) &&
                        (it.statut == Statut.DECOLLE || it.statut == Statut.ENVOL)
            }
            .map { convertirEnVolTrafficDTO(it) }
    }

    fun getHistoriqueVol(volId: Long): HistoriqueResponseDTO? {
        return try {
            historiqueAdapter.searchByIDVol(volId)
                ?.let { historiqueMapperDomain.domainToResponse(it) }
        } catch (e: Exception) {
            null
        }
    }

    fun getHistoriquesAeroport(aeroportId: Long): Collection<HistoriqueResponseDTO> {
        val tousLesVols = volAdapter.getAllVols()
        val volsAeroport = tousLesVols.filter {
            it.origine?.id == aeroportId || it.destination?.id == aeroportId
        }

        return volsAeroport.mapNotNull { vol ->
            vol.id?.let { volId ->
                try {
                    historiqueAdapter.searchByIDVol(volId)
                        ?.let { historiqueMapperDomain.domainToResponse(it) }
                } catch (e: Exception) {
                    null
                }
            }
        }
    }

    fun getPlanningsPistesAeroport(aeroportId: Long): Collection<PlanningPistesResponseDTO> {
        // On récupère pour l'instant tous les plannings de toutes les pistes,
        // comme dans l'implémentation d'origine.
        val toutesLesPistes = pisteAdapter.getAllPiste()

        return toutesLesPistes.flatMap { piste ->
            piste.id?.let { pisteId ->
                planningPistesAdapter.getPlanningPisteByPisteId(pisteId)
                    .map { planningPistesMapperDomain.domainToResponse(it) }
            } ?: emptyList()
        }
    }

    fun getPlanningPiste(pisteId: Long): Collection<PlanningPistesResponseDTO> {
        return planningPistesAdapter.getPlanningPisteByPisteId(pisteId)
            .map { planningPistesMapperDomain.domainToResponse(it) }
    }

    private fun calculerStatistiques(
        volsDepart: Collection<VolTrafficDTO>,
        volsArrivee: Collection<VolTrafficDTO>,
        pistes: Collection<PisteDomain>,
        hangars: Collection<HangarDomain>
    ): StatistiquesTrafficDTO {

        val tousLesVols = (volsDepart + volsArrivee).distinctBy { it.id }

        val volsEnCours = tousLesVols.count {
            it.statut == Statut.DECOLLE || it.statut == Statut.ENVOL
        }

        val volsRetardes = tousLesVols.count { it.statut == Statut.RETARDE }
        val volsAnnules = tousLesVols.count { it.statut == Statut.ANNULE }

        val tauxOccupationPistes = if (pistes.isNotEmpty()) {
            pistes.count { it.etat == Etat.OCCUPE }.toDouble() / pistes.size * 100
        } else 0.0

        val capaciteTotaleHangars = hangars.sumOf { it.capacite }
        val avionsStockes = hangars.sumOf { it.avionEntities.size }
        val tauxOccupationHangars = if (capaciteTotaleHangars > 0) {
            avionsStockes.toDouble() / capaciteTotaleHangars * 100
        } else 0.0

        return StatistiquesTrafficDTO(
            totalVolsDepart = volsDepart.size,
            totalVolsArrivee = volsArrivee.size,
            volsEnCours = volsEnCours,
            volsRetardes = volsRetardes,
            volsAnnules = volsAnnules,
            tauxOccupationPistes = tauxOccupationPistes,
            tauxOccupationHangars = tauxOccupationHangars
        )
    }

    private fun convertirEnVolTrafficDTO(vol: VolDomain): VolTrafficDTO {
        val retard = if (vol.statut == Statut.RETARDE) {
            0 // la table d'historique permettrait d'affiner si besoin
        } else null

        return VolTrafficDTO(
            id = vol.id,
            numeroVol = vol.numeroVol,
            compagnie = vol.compagnie,
            origineId = vol.origine?.id,
            origineNom = vol.origine?.nom,
            origineCodeIATA = vol.origine?.codeIATA,
            destinationId = vol.destination?.id,
            destinationNom = vol.destination?.nom,
            destinationCodeIATA = vol.destination?.codeIATA,
            dateDepart = vol.dateDepart,
            dateArrivee = vol.dateArrivee,
            statut = vol.statut,
            pisteDecollageId = vol.pisteDecollage?.id,
            pisteAtterissageId = vol.pisteAtterissage?.id,
            avionId = vol.avionEntity?.id,
            retard = retard
        )
    }

    /**
     * Vols de trafic BJA (tous les vols sortants) – on ne filtre pas sur Anways.
     */
    fun getVolsDepartBja(): List<VolTrafficDTO> {
        return externalVolBjaAdapter.getDeparts().map { ext ->
            convertirVolExterneEnTrafficDTO(ext)
        }
    }

    /**
     * Vols de trafic BJA (tous les vols entrants) – on ne filtre pas sur Anways.
     */
    fun getVolsArriveeBja(): List<VolTrafficDTO> {
        return externalVolBjaAdapter.getArrivees().map { ext ->
            convertirVolExterneEnTrafficDTO(ext)
        }
    }

    private fun convertirVolExterneEnTrafficDTO(ext: VolExterneBJAResponseDTO): VolTrafficDTO {
        val origine = ext.origine
        val destination = ext.destination

        return VolTrafficDTO(
            id = null,
            numeroVol = ext.numeroVol,
            compagnie = "",
            origineId = null,
            origineNom = origine,
            origineCodeIATA = origine,
            destinationId = null,
            destinationNom = destination,
            destinationCodeIATA = destination,
            dateDepart = ext.heureDepart,
            dateArrivee = ext.heureArrivee,
            statut = mapStatutExterne(ext.statut),
            pisteDecollageId = null,
            pisteAtterissageId = null,
            avionId = null,
            retard = null
        )
    }

    /**
     * Mapping des statuts BJA -> statuts internes Anways.
     *
     * PROGRAMME , ENREGISTREMENT EMBARQUEMENT PRET_DECOLLAGE
     * DECOLLE EN_VOL EN_APPROCHE ATTERI RETARDE DETOURNE ANNULE
     */
    private fun mapStatutExterne(statutExterne: String): Statut {
        val normalized = statutExterne.trim().uppercase().replace(' ', '_')
        return when (normalized) {
            "PROGRAMME" -> Statut.PREVU
            "ENREGISTREMENT" -> Statut.ENATTENTE
            "EMBARQUEMENT" -> Statut.EMBARQUEMENT
            "PRET_DECOLLAGE" -> Statut.ENATTENTE
            "DECOLLE" -> Statut.DECOLLE
            "EN_VOL" -> Statut.ENVOL
            "EN_APPROCHE" -> Statut.ENVOL
            "ATTERI" -> Statut.ARRIVE
            "RETARDE" -> Statut.RETARDE
            "DETOURNE" -> Statut.ANNULE
            "ANNULE" -> Statut.ANNULE
            else -> Statut.PREVU
        }
    }
}