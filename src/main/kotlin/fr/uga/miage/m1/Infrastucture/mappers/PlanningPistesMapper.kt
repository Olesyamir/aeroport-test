package fr.uga.miage.m1.Infrastucture.mappers

import fr.uga.miage.m1.Domain.domains.PlanningPistesDomain
import fr.uga.miage.m1.Infrastucture.models.PlanningPistesEntity
import fr.uga.miage.m1.Domain.updateRequest.UpdatePlanningPistesRequest
import org.springframework.stereotype.Component

@Component
class PlanningPistesMapper {

    fun domainToEntity(
        planningPistes : PlanningPistesDomain
    ): PlanningPistesEntity =
        PlanningPistesEntity(
            id = planningPistes.id,
            startTime = planningPistes.startTime,
            endTime = planningPistes.endTime,
            priorite = planningPistes.priorite,
            remarques = planningPistes.remarques,
            usage = planningPistes.usage,
            volsDepart = planningPistes.volsDepart,
            volsArrivee = planningPistes.volsArrivee,
            pisteId = planningPistes.pisteId,
        )

    fun entityToDomain(
        planningPistes: PlanningPistesEntity,
    ): PlanningPistesDomain =
        PlanningPistesDomain(
            id = planningPistes.id,
            startTime = planningPistes.startTime,
            endTime = planningPistes.endTime,
            priorite = planningPistes.priorite,
            remarques = planningPistes.remarques,
            usage = planningPistes.usage,
            volsDepart = planningPistes.volsDepart,
            volsArrivee = planningPistes.volsArrivee,
            pisteId = planningPistes.pisteId,
        )

    fun toUpdate(update: UpdatePlanningPistesRequest, planningPistes: PlanningPistesEntity) {
        update.priorite.let { planningPistes.priorite = it }
        update.remarques.let { planningPistes.remarques = it }
        update.pisteId?.let { planningPistes.pisteId = it }
        update.startTime.let { planningPistes.startTime = it }
        update.endTime.let { planningPistes.endTime = it }
        update.usage.let { planningPistes.usage = it }
    }
}
