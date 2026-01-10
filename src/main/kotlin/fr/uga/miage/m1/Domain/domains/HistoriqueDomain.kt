package fr.uga.miage.m1.Domain.domains
import fr.uga.miage.m1.Domain.enums.Statut
import java.time.LocalDateTime

data class HistoriqueDomain(

    val id: Long?,
    val statut: Statut,
    val datetime: LocalDateTime?,
    val idVol: Long?,
)