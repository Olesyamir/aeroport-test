package fr.uga.miage.m1.Domain.domains
import fr.uga.miage.m1.Domain.enums.Statut
import fr.uga.miage.m1.Domain.enums.TypeVol
import fr.uga.miage.m1.Infrastucture.models.AvionEntity
import fr.uga.miage.m1.Infrastucture.models.PisteEntity
import fr.uga.miage.m1.Infrastucture.models.PlanningPistesEntity
import fr.uga.miage.m1.models.AeroportEntity
import java.time.LocalDateTime

data class VolDomain (

    val id: Long?,
    var numeroVol: String,
    var compagnie: String,
    var origine: AeroportEntity? = null,
    var destination: AeroportEntity? = null,
    var dateDepart: LocalDateTime,
    var dateArrivee: LocalDateTime,
    var statut: Statut,
    var typeVol: TypeVol,
    var avionEntity: AvionEntity? = null,
    var pisteDecollage : PisteEntity? = null,
    var pisteAtterissage : PisteEntity? = null,
    var planningPistesEntity: PlanningPistesEntity? = null,

    )