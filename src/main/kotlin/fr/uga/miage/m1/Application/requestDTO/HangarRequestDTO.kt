package fr.uga.miage.m1.Application.requestDTO
import fr.uga.miage.m1.Domain.enums.Etat

data class HangarRequestDTO (
    val capacite: Int,
    val etat: Etat,
    val avionIds:  Collection<Long>?= null,

)

