package fr.uga.miage.m1.Application.requestDTO

import fr.uga.miage.m1.Domain.enums.Priorite
import fr.uga.miage.m1.Domain.enums.Usage
import java.time.LocalTime

data class PlanningPistesRequestDTO (
    val startTime: LocalTime,
    val endTime: LocalTime,
    val priorite: Priorite,
    val remarques: String?,
    val volIds : Collection<Long>?= null,
    val usage: Usage,

)