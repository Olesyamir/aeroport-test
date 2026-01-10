package fr.uga.miage.m1.Application.responseDTO

import fr.uga.miage.m1.Domain.enums.EtatMateriel

data class AvionResponseDTO (
    val id: Long?,
    val nom: String,
    val numImmatricule: String,
    val type: String,
    val capacite: Int,
    val etat: EtatMateriel,
    val hangarId: Long?,
    val pisteId: Long?,
    val volIds: Collection<Long>?
)
