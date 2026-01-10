package fr.uga.miage.m1.factory

import fr.uga.miage.m1.adapters.AeroportAdapter
import fr.uga.miage.m1.Application.builders.VolBuilder
import fr.uga.miage.m1.Application.mapperDomain.VolMapperDomain
import fr.uga.miage.m1.Application.requestDTO.VolRequestDTO
import fr.uga.miage.m1.Domain.domains.VolDomain
import fr.uga.miage.m1.Domain.enums.Statut
import fr.uga.miage.m1.Infrastucture.adapters.AvionAdapter
import fr.uga.miage.m1.Infrastucture.adapters.PisteAdapter
import fr.uga.miage.m1.Infrastucture.adapters.VolAdapter
import fr.uga.miage.m1.Infrastucture.mappers.AvionMapper
import fr.uga.miage.m1.Infrastucture.mappers.PisteMapper
import fr.uga.miage.m1.Infrastucture.mappers.VolMapper
import fr.uga.miage.m1.Infrastucture.models.PisteEntity
import fr.uga.miage.m1.mappers.AeroportMapper
import fr.uga.miage.m1.Application.aeroportCentrale.CentralAirport
import org.springframework.stereotype.Component
import kotlin.math.abs

@Component
class VolFactory(
    private val volMapper: VolMapper,
    private val volMapperDomain: VolMapperDomain,
    private val volAdapter: VolAdapter,
    private val avionMapper: AvionMapper,
    private val pisteMapper: PisteMapper,
    private val avionAdapter: AvionAdapter,
    private val pisteAdapter: PisteAdapter,
    private val aeroportAdapter: AeroportAdapter,
    private val aeroportMapper: AeroportMapper
) {

    fun create(request: VolRequestDTO): VolDomain {

        val num = volAdapter.searchByNumVol(request.numeroVol)
        require(num == null) {
            throw IllegalArgumentException("Un vol avec ${request.numeroVol} existe déjà")
        }

        require(!(request.dateArrivee.isBefore(request.dateDepart))) {
            throw IllegalArgumentException("La date de départ doit être avant la date d'arrivée.")
        }

        // Origine : aéroport central unique
        val flightTogetherDomain = aeroportAdapter.searchByCodeIATA(CentralAirport.CODE_IATA)
            ?: throw IllegalStateException("L'aéroport central (${CentralAirport.CODE_IATA}) est introuvable.")
        val origineEntity = aeroportMapper.domainToEntity(flightTogetherDomain)

        val destinationEntity = request.destinationId.let { destinationId ->
            val aeroportDomain = aeroportAdapter.getAeroportById(destinationId)
            aeroportMapper.domainToEntity(aeroportDomain)
        }

        val avionEntity = request.avionId?.let { avionId ->
            val avionDomain = avionAdapter.getAvionById(avionId)
            avionMapper.domainToEntity(avionDomain)
        }

        val pisteDecollageEntity: PisteEntity? = request.pisteDecollageId?.let { pisteId ->
            val pisteDomain = pisteAdapter.getPisteById(pisteId)
            val volsMemePiste = volAdapter.getAllVols()
                .filter { it.pisteDecollage?.id == pisteDomain.id && it.statut != Statut.ANNULE }

            require(!(volsMemePiste.any { vol ->
                request.dateDepart == vol.dateDepart ||
                        (request.dateDepart.toLocalDate() == vol.dateDepart.toLocalDate()
                                && request.dateDepart.hour == vol.dateDepart.hour
                                && abs(request.dateDepart.minute - vol.dateDepart.minute) <= 5)
            })) {
                throw IllegalArgumentException("Conflit de planning sur la piste de décollage $pisteId; l'affectation ne peut pas être faite")
            }
            pisteMapper.domainToEntity(pisteDomain)
        }

        val pisteAtterissageEntity = request.pisteAtterissageId?.let { pisteId ->
            val pisteDomain = pisteAdapter.getPisteById(pisteId)
            val volsMemePiste = volAdapter.getAllVols()
                .filter { it.pisteAtterissage?.id == pisteDomain.id && it.statut != Statut.ANNULE }

            require(!(volsMemePiste.any { vol ->
                request.dateDepart == vol.dateDepart ||
                        (request.dateDepart.toLocalDate() == vol.dateDepart.toLocalDate()
                                && request.dateDepart.hour == vol.dateDepart.hour
                                && abs(request.dateDepart.minute - vol.dateDepart.minute) <= 5)
            })) {
                throw IllegalArgumentException("Conflit de planning sur la piste d'atterrissage $pisteId; l'affectation ne peut pas être faite")
            }
            pisteMapper.domainToEntity(pisteDomain)
        }


        val volDomain = VolBuilder()
            .withNumeroVol(request.numeroVol)
            .withCompagnie(request.compagnie)
            .withOrigine(origineEntity)
            .withDestination(destinationEntity)
            .withDates(request.dateDepart, request.dateArrivee)
            .withStatut(request.statut)
            .withTypeVol(request.typeVol)
            .withAvion(avionEntity)
            .withPisteDecollage(pisteDecollageEntity)
            .withPisteAtterissage(pisteAtterissageEntity)
            .build()

        val volEntity = volMapper.domainToEntity(volDomain)
        val saveVolDomain = volAdapter.saveVol(volEntity)
            ?: throw IllegalStateException("Erreur lors de la sauvegarde du vol")
        return saveVolDomain
    }
}
