package fr.uga.miage.m1.Domain.updateRequest

import fr.uga.miage.m1.Domain.enums.Etat

class UpdatePisteRequest (
    val etat: Etat,
    val longueur : Double,
    val avionIds:  Collection<Long>? = null,
    val aeroportId: Long? = null,
//    val avionEntities : Collection<AvionEntity>? = null,
    val volsDepartIds: Collection<Long>? = null,
    val volsArriveeIds: Collection<Long>? = null,
)
