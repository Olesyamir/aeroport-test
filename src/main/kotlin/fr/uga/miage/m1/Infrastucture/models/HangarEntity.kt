package fr.uga.miage.m1.Infrastucture.models

import fr.uga.miage.m1.Domain.enums.Etat
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Enumerated
import jakarta.persistence.EnumType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany

@Entity
class HangarEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, unique = true)
    val id : Long?=0,

    @Column(nullable = false)
    var capacite : Int=0,

    @Enumerated(EnumType.STRING)
    var etat : Etat=Etat.LIBRE,

    @OneToMany(mappedBy = "hangarEntity")
    var avionEntities : MutableList<AvionEntity> = mutableListOf(),
)
