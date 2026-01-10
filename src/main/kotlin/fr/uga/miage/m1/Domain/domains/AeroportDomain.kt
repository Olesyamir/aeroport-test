package fr.uga.miage.m1.domains

import fr.uga.miage.m1.Infrastucture.models.VolEntity


class AeroportDomain(
    val id: Long? = null,
    var nom: String,
    var ville: String,
    var pays: String,
    var codeIATA: String,
    var volsDepart: MutableList<VolEntity> = mutableListOf(),
    var volsArrivee: MutableList<VolEntity> = mutableListOf()
)
