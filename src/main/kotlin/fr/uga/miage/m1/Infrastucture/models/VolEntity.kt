package fr.uga.miage.m1.Infrastucture.models

import fr.uga.miage.m1.Domain.enums.Statut
import fr.uga.miage.m1.Domain.enums.TypeVol
import fr.uga.miage.m1.models.AeroportEntity
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "vol")
class VolEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, unique = true)
    val id: Long?= null,

    @Column(nullable = false, unique = true)
    var numeroVol: String="",

    var compagnie: String="",

    @ManyToOne
    var origine: AeroportEntity? = null,

    @ManyToOne
    var destination: AeroportEntity? = null,

    var dateDepart: LocalDateTime = LocalDateTime.now(),

    var dateArrivee: LocalDateTime=LocalDateTime.now(),

    @Enumerated(EnumType.STRING)
    var statut: Statut= Statut.PREVU,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var typeVol: TypeVol= TypeVol.NORMAL,

    @ManyToOne
    var avionEntity: AvionEntity? = null,

    @ManyToOne
    var pisteDecollage : PisteEntity? = null,

    @ManyToOne
    var pisteAtterissage : PisteEntity? = null,

    @ManyToOne
    var planningPistesEntity: PlanningPistesEntity? = null,

    ){

}