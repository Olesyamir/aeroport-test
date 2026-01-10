package fr.uga.miage.m1.Application.responseDTO

import fr.uga.miage.m1.Domain.enums.Priorite
import fr.uga.miage.m1.Domain.enums.Statut
import fr.uga.miage.m1.Domain.enums.Usage
import java.time.LocalTime

data class PlanningPistesResponseDTO (
    val id: Long?,
    val startTime: LocalTime?,
    val endTime: LocalTime?,
    val priorite: Priorite,
    val remarques: String?,
    val volsDepartId: Collection<Long>? = null,
    val volsArriveeId: Collection<Long>? = null,
    val volStatut: Collection<Statut>? = null,
    val usage: Usage,
    val pisteId: Long? = null,
    //val avions : Collection<Long>?,

    )
