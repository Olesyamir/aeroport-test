package fr.uga.miage.m1.Application.responseDTO

import java.time.LocalDateTime

data class VolExterneBJAResponseDTO (
    val numeroVol: String,
    val origine: String,
    val destination: String,
    val heureDepart: LocalDateTime,
    val heureArrivee: LocalDateTime,
    val statut: String,
    val avionImmatriculation: String?,
    val pisteAssignee: String?
    )