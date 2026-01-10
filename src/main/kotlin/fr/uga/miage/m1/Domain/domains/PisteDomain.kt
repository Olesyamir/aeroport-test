package fr.uga.miage.m1.Domain.domains
import fr.uga.miage.m1.Domain.enums.Etat
import fr.uga.miage.m1.Infrastucture.models.VolEntity


data class PisteDomain (

    val id: Long?,
    var longueur: Double,
    var etat: Etat,
    var volsDepart: MutableList<VolEntity> = mutableListOf(),
    var volsArrivee: MutableList<VolEntity> = mutableListOf()
)
