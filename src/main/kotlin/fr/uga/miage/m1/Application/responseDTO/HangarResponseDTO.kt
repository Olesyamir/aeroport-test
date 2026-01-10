package fr.uga.miage.m1.Application.responseDTO

import fr.uga.miage.m1.Domain.enums.Etat

data class HangarResponseDTO (
    val id: Long?,
    val capacite: Int,
    val etat: Etat,
    val avions: Collection<Long>?= null
    )
