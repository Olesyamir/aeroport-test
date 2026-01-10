package fr.uga.miage.m1.Infrastucture.models

import fr.uga.miage.m1.Domain.enums.Priorite
import fr.uga.miage.m1.Domain.enums.Usage
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import java.time.LocalTime


@Entity
class PlanningPistesEntity (

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long?= null,

    var startTime: LocalTime?= LocalTime.now(),
    var endTime: LocalTime?= LocalTime.now(),

    @Enumerated(EnumType.STRING)
    var priorite: Priorite= Priorite.NORMALE,

    var remarques: String?="",

    @Enumerated(EnumType.STRING)
    var usage: Usage= Usage.DECOLLAGE,

    @OneToMany(mappedBy = "planningPistesEntity")
    var volsDepart: MutableList<VolEntity> = mutableListOf(),

    @OneToMany(mappedBy = "planningPistesEntity")
    var volsArrivee: MutableList<VolEntity> = mutableListOf(),

    var pisteId : Long? = null,


    ){

}