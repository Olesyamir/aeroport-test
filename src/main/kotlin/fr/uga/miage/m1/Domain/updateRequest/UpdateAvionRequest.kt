package fr.uga.miage.m1.Domain.updateRequest
import fr.uga.miage.m1.Domain.enums.EtatMateriel

data class UpdateAvionRequest(
    val nom: String,
    val numImmatricule: String,
    val type: String,
    val etat: EtatMateriel,
    val capacite: Int ,
    val hangarId : Long? = null,
    val pisteId : Long? = null,
    val volsId : Collection<Long>? = null,
)


