package fr.uga.miage.m1.Application.responseDTO

import fr.uga.miage.m1.Domain.enums.Statut
import java.time.LocalDateTime

data class HistoriqueResponseDTO (
    val id: Long?,
    val statut: Statut,
    val datetime: LocalDateTime?,
    val idVol: Long?,
//    val numVol: Long?,
)