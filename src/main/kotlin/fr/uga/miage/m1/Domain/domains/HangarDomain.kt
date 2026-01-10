package fr.uga.miage.m1.Domain.domains
import fr.uga.miage.m1.Domain.enums.Etat
import fr.uga.miage.m1.Infrastucture.models.AvionEntity

data class HangarDomain(

    val id : Long?,

    val capacite : Int,

    val etat : Etat,
    var avionEntities : MutableList<AvionEntity> = mutableListOf()
)
