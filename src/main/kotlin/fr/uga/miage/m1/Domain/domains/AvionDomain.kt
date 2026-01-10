package fr.uga.miage.m1.Domain.domains
import fr.uga.miage.m1.Domain.enums.EtatMateriel
import fr.uga.miage.m1.Infrastucture.models.HangarEntity
import fr.uga.miage.m1.Infrastucture.models.PisteEntity
import fr.uga.miage.m1.Infrastucture.models.VolEntity


data class AvionDomain(
    val id: Long?,

    val nom: String,

    val numImmatricule: String,

    val type: String,

    val capacite: Int,

    val etat: EtatMateriel,

    val hangarEntity: HangarEntity? = null,

    val pisteEntity: PisteEntity? = null,

    var volEntity: MutableList<VolEntity> = mutableListOf()
)