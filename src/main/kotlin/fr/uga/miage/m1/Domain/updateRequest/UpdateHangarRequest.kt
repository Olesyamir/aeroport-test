package fr.uga.miage.m1.Domain.updateRequest

import fr.uga.miage.m1.Domain.enums.Etat

class UpdateHangarRequest (
    val etat: Etat,
    val capacite : Int,
    val avionIds :  Collection<Long>?= null
    )


