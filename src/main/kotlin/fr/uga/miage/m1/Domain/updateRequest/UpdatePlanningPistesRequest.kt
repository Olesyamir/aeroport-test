package fr.uga.miage.m1.Domain.updateRequest

import fr.uga.miage.m1.Domain.enums.Priorite
import fr.uga.miage.m1.Domain.enums.Usage
import java.time.LocalTime

data class UpdatePlanningPistesRequest (
    val startTime: LocalTime,
    val endTime: LocalTime,
    val priorite: Priorite,
    val remarques: String?,
    val usage: Usage,
    val pisteId : Long? = null,
    val volsId : Collection<Long>? = null,
)