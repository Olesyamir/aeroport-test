package fr.uga.miage.m1.Application.responseDTO

import fr.uga.miage.m1.Domain.enums.Etat

data class PisteResponseDTO (
    val id: Long?,
    val longueur: Double,
    val etat: Etat,
//    val avions: Collection<Long>?= null,
    val volsDepart : Collection<Long>?=null,
    val volsArrivee : Collection<Long>?=null
    )

