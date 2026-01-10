package fr.uga.miage.m1.Domain.domains
import fr.uga.miage.m1.Domain.enums.Priorite
import fr.uga.miage.m1.Domain.enums.Usage
import fr.uga.miage.m1.Infrastucture.models.VolEntity
import java.time.LocalTime


class PlanningPistesDomain (

    val id: Long?,
    val startTime: LocalTime?,
    val endTime: LocalTime?,
    val priorite: Priorite,
    val remarques: String?,
    val usage: Usage,
    val pisteId: Long? = null,
    val volsDepart: MutableList<VolEntity> = mutableListOf(),
    val volsArrivee: MutableList<VolEntity> = mutableListOf(),

    )