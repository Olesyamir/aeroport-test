package fr.uga.miage.m1.Application.requestDTO

import fr.uga.miage.m1.Domain.enums.Etat

data class PisteRequestDTO(
    val longueur : Double,
    val etat: Etat,
    val volsDepartIds: Collection<Long>? = null,
    val volsArriveeIds: Collection<Long>? = null,
    )

