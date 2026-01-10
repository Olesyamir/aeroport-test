package fr.uga.miage.m1.Application.mapperDomain

import fr.uga.miage.m1.Domain.domains.AvionDomain
import fr.uga.miage.m1.Infrastucture.models.HangarEntity
import fr.uga.miage.m1.Infrastucture.models.VolEntity
import fr.uga.miage.m1.Application.responseDTO.AvionResponseDTO
import fr.uga.miage.m1.Application.requestDTO.AvionRequestDTO
import org.springframework.stereotype.Component

@Component
class AvionMapperDomain {

    fun requestToDomain (
        request: AvionRequestDTO,
        hangar: HangarEntity?,
        vols: MutableList<VolEntity> = mutableListOf(),
    ) : AvionDomain =
        AvionDomain(
            //  request n'a pas d'id
            id = null,
            nom = request.nom,
            numImmatricule = request.numImmatricule,
            type = request.type,
            capacite = request.capacite,
            etat = request.etat,
            hangarEntity = hangar,
            volEntity = vols,
        )


    fun domainToResponse (domain : AvionDomain) : AvionResponseDTO =
        AvionResponseDTO(
            id = domain.id,
            nom = domain.nom,
            numImmatricule = domain.numImmatricule,
            type = domain.type,
            capacite = domain.capacite,
            etat = domain.etat,
            hangarId = domain.hangarEntity?.id,
            pisteId = domain.pisteEntity?.id,
            volIds = domain.volEntity.mapNotNull { it.id }
        )
}