package fr.uga.miage.m1.Infrastucture.models

import fr.uga.miage.m1.Domain.enums.Statut
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "historique_entity")
class HistoriqueEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    val id: Long? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var statut: Statut = Statut.PREVU,

    var datetime: LocalDateTime? = LocalDateTime.now(),

    @Column(name = "id_vol")
    var idVol: Long? = null
)
