package fr.uga.miage.m1.Application.mapperDomain

import fr.uga.miage.m1.Domain.domains.PlanningPistesDomain
import fr.uga.miage.m1.Infrastucture.models.PisteEntity
import fr.uga.miage.m1.Infrastucture.models.VolEntity
import fr.uga.miage.m1.Application.responseDTO.PlanningPistesResponseDTO
import fr.uga.miage.m1.Application.requestDTO.PlanningPistesRequestDTO
import org.springframework.stereotype.Component

@Component
class PlanningPistesMapperDomain {

    fun requestToDomain(
        request: PlanningPistesRequestDTO,
        volsDepart: MutableList<VolEntity> = mutableListOf(),
        volsArrivee: MutableList<VolEntity> = mutableListOf(),
        piste: PisteEntity? = null
        ): PlanningPistesDomain =
        PlanningPistesDomain(
            id = null,
            startTime = request.startTime,
            endTime = request.endTime,
            priorite = request.priorite,
            remarques = request.remarques,
            usage = request.usage,
            volsDepart = volsDepart,
            volsArrivee = volsArrivee,
            pisteId = piste?.id,
        )


    fun domainToResponse(
        domain : PlanningPistesDomain,
    ): PlanningPistesResponseDTO =
        PlanningPistesResponseDTO(
            id = domain.id,
            startTime = domain.startTime,
            endTime = domain.endTime,
            priorite = domain.priorite,
            remarques = domain.remarques,
            usage = domain.usage,
            volsDepartId = domain.volsDepart.mapNotNull { it.id },
            volsArriveeId = domain.volsArrivee.mapNotNull { it.id },
            pisteId = domain.pisteId,
        )

}
