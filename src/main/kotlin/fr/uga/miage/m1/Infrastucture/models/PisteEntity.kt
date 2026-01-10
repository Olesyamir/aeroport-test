package fr.uga.miage.m1.Infrastucture.models

import fr.uga.miage.m1.Domain.enums.Etat
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany


@Entity
class PisteEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, unique = true)
    val id : Long?= null,

    @Column(nullable = false)
    var longueur : Double = 0.0,

    @Enumerated(EnumType.STRING)
    var etat : Etat = Etat.LIBRE,

    @OneToMany(mappedBy="pisteDecollage")
    var volsArrivee : MutableList<VolEntity> = mutableListOf(),

    @OneToMany(mappedBy="pisteAtterissage")
    var volsDepart : MutableList<VolEntity> = mutableListOf(),
)
