package fr.uga.miage.m1.Application.responseDTO

import fr.uga.miage.m1.Domain.enums.Statut
import fr.uga.miage.m1.Domain.enums.TypeVol
import java.time.LocalDateTime


data class VolResponseDTO (
    val id: Long?,
    val numeroVol: String,
    val compagnie: String,
    val origineId: Long?,
    val destinationId: Long?,
    val dateDepart: LocalDateTime,
    val dateArrivee: LocalDateTime,
    val statut: Statut,
    val typeVol: TypeVol,
    val avionId: Long? = null,
    val pisteDecollageId: Long? = null,
    val pisteAtterissageId: Long? = null,
    )