package fr.uga.miage.m1.Domain.updateRequest

import fr.uga.miage.m1.Domain.enums.Statut
import fr.uga.miage.m1.Domain.enums.TypeVol
import java.time.LocalDateTime

data class UpdateVolRequest(
    val numeroVol: String? = null,
    val compagnie: String? = null,
    val origineId: Long? = null,
    val destinationId: Long? = null,
    val dateDepart: LocalDateTime,
    val dateArrivee: LocalDateTime,
    val statut: Statut,
    val typeVol: TypeVol? = null,
    val avionId: Long? = null,
    val pisteDecollageId: Long? = null,
    val pisteAtterissageId: Long? = null
)