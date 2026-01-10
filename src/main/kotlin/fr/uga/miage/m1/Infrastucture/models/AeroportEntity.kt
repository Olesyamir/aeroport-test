package fr.uga.miage.m1.models

import fr.uga.miage.m1.Infrastucture.models.VolEntity
import jakarta.persistence.*

@Entity
@Table(name = "aeroport")
class AeroportEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, unique = true)
    val id: Long? = null,

    @Column(nullable = false, unique = true)
    var nom: String = "",

    @Column(nullable = false)
    var ville: String = "",

    @Column(nullable = false)
    var pays: String = "",

    @Column(nullable = false, unique = true, length = 3)
    var codeIATA: String = "",


    @OneToMany(mappedBy = "origine")
    var volsDepart: MutableList<VolEntity> = mutableListOf(),

    @OneToMany(mappedBy = "destination")
    var volsArrivee: MutableList<VolEntity> = mutableListOf()
)
