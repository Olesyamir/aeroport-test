package fr.uga.miage.m1.Application.requestDTO

import fr.uga.miage.m1.Domain.enums.Statut

data class HistoriqueRequestDTO (
    val idVol : Long? = null,
    val statut: Statut,
)