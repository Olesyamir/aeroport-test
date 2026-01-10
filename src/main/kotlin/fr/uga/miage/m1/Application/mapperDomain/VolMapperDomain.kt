package fr.uga.miage.m1.Application.mapperDomain

import fr.uga.miage.m1.models.AeroportEntity
import fr.uga.miage.m1.Domain.domains.VolDomain
import fr.uga.miage.m1.Infrastucture.models.AvionEntity
import fr.uga.miage.m1.Infrastucture.models.PisteEntity
import fr.uga.miage.m1.Application.responseDTO.VolResponseDTO
import fr.uga.miage.m1.Application.requestDTO.VolRequestDTO
import org.springframework.stereotype.Component

@Component
class VolMapperDomain {

    fun requestToDomain(
        request: VolRequestDTO,
        avion: AvionEntity?,
        pisteDecollage : PisteEntity?,
        pisteAtterissage : PisteEntity?,
        origine: AeroportEntity? = null,
        destination: AeroportEntity? = null
    ): VolDomain =
        VolDomain(
            id = null,
            numeroVol = request.numeroVol,
            compagnie = request.compagnie,
            origine = origine,
            destination = destination,
            dateDepart = request.dateDepart,
            dateArrivee = request.dateArrivee,
            statut = request.statut,
            typeVol = request.typeVol,
            avionEntity = avion,
            pisteDecollage = pisteDecollage,
            pisteAtterissage = pisteAtterissage,
        )


    fun domainToResponse(
        domain : VolDomain,
    ) : VolResponseDTO=
        VolResponseDTO(
            id = domain.id,
            numeroVol = domain.numeroVol,
            compagnie = domain.compagnie,
            origineId = domain.origine?.id,
            destinationId = domain.destination?.id,
            dateDepart = domain.dateDepart,
            dateArrivee = domain.dateArrivee,
            statut = domain.statut,
            typeVol = domain.typeVol,
            avionId = domain.avionEntity?.id,
            pisteDecollageId = domain.pisteDecollage?.id,
            pisteAtterissageId = domain.pisteAtterissage?.id

        );
}