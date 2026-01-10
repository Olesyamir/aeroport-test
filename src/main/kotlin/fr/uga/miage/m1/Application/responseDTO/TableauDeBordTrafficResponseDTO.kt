package fr.uga.miage.m1.reponses

import fr.uga.miage.m1.Domain.enums.Statut
import java.time.LocalDateTime

data class TableauDeBordTrafficDTO(
    val aeroportId: Long,
    val aeroportNom: String,
    val aeroportCodeIATA: String,

    val statistiques: StatistiquesTrafficDTO,
    val volsDepart: List<VolTrafficDTO>,

    val volsArrivee: List<VolTrafficDTO>,

    val pistesDisponibles: Int,
    val pistesOccupees: Int,
    val pistesEnMaintenance: Int,

    val hangarsDisponibles: Int,
    val hangarsOccupes: Int,

    val timestamp: LocalDateTime = LocalDateTime.now()
)


data class StatistiquesTrafficDTO(
    val totalVolsDepart: Int,
    val totalVolsArrivee: Int,
    val volsEnCours: Int,
    val volsRetardes: Int,
    val volsAnnules: Int,
    val tauxOccupationPistes: Double,
    val tauxOccupationHangars: Double
)


data class VolTrafficDTO(
    val id: Long?,
    val numeroVol: String,
    val compagnie: String,
    val origineId: Long?,
    val origineNom: String?,
    val origineCodeIATA: String?,
    val destinationId: Long?,
    val destinationNom: String?,
    val destinationCodeIATA: String?,
    val dateDepart: LocalDateTime,
    val dateArrivee: LocalDateTime,
    val statut: Statut,
    val pisteDecollageId: Long?,
    val pisteAtterissageId: Long?,
    val avionId: Long?,
    val retard: Int? = null  // en minutes
)