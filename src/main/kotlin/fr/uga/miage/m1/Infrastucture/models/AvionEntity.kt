package fr.uga.miage.m1.Infrastucture.models

import fr.uga.miage.m1.Domain.enums.EtatMateriel
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Enumerated
import jakarta.persistence.EnumType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany


@Entity
class AvionEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, unique = true)
    val id : Long?=0,

    @Column(nullable = false)
    var nom: String="",

    @Column(nullable = false, unique = true)
    var numImmatricule: String="",

    var type: String="",

    var capacite: Int=0,

    @Enumerated(EnumType.STRING)
    var etat: EtatMateriel= EtatMateriel.AUSOL,

    @ManyToOne()
    @JoinColumn(name = "hangar_id")
    var hangarEntity : HangarEntity? = null,

    @OneToMany(mappedBy="avionEntity")
    var volEntity: MutableList<VolEntity> = mutableListOf()
){

}