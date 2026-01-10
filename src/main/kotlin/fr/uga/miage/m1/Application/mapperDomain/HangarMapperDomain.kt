package fr.uga.miage.m1.Application.mapperDomain

import fr.uga.miage.m1.Domain.domains.HangarDomain
import fr.uga.miage.m1.Infrastucture.models.AvionEntity
import fr.uga.miage.m1.Application.responseDTO.HangarResponseDTO
import fr.uga.miage.m1.Application.requestDTO.HangarRequestDTO
import org.springframework.stereotype.Component

@Component
class HangarMapperDomain {

    fun requestToDomain(
        request: HangarRequestDTO,
        avions: Collection<AvionEntity>
    ): HangarDomain =
        HangarDomain(
            id = null,
            capacite = request.capacite,
            etat = request.etat,
            avionEntities = avions.toMutableList()
        )

    fun domainToResponse(domain: HangarDomain): HangarResponseDTO =
        HangarResponseDTO(
            id = domain.id,
            capacite = domain.capacite,
            etat = domain.etat,
            avions = domain.avionEntities.mapNotNull { it.id }
        )
}
