package fr.uga.miage.m1.Application.requestDTO
import fr.uga.miage.m1.Domain.enums.EtatMateriel

data class AvionRequestDTO(
    val nom: String,
    val numImmatricule: String,
    val type: String,
    val capacite: Int,
    val etat: EtatMateriel,
    val hangarId : Long? = null,
    val pisteId : Long? = null,
    val volIds:  Collection<Long>?= null
)