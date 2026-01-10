package fr.uga.miage.m1.Application.builders


import fr.uga.miage.m1.Domain.domains.VolDomain
import fr.uga.miage.m1.Domain.enums.Statut
import fr.uga.miage.m1.Domain.enums.TypeVol
import fr.uga.miage.m1.Infrastucture.models.AvionEntity
import fr.uga.miage.m1.Infrastucture.models.PisteEntity
import fr.uga.miage.m1.models.AeroportEntity
import java.time.LocalDateTime


class VolBuilder {
    private var id: Long? = null
    private var numeroVol: String = ""
    private var compagnie: String = ""
    private var dateDepart: LocalDateTime = LocalDateTime.now()
    private var dateArrivee: LocalDateTime = LocalDateTime.now().plusHours(2)
    private var statut: Statut = Statut.PREVU
    private var typeVol: TypeVol = TypeVol.NORMAL
    private var origine: AeroportEntity? = null
    private var destination: AeroportEntity? = null
    private var avionEntity: AvionEntity? = null
    private var pisteDecollage: PisteEntity? = null
    private var pisteAtterissage: PisteEntity? = null

    fun withId(id: Long?) = apply { this.id = id }

    fun withNumeroVol(numeroVol: String) = apply {
        require(numeroVol.isNotBlank()) { "Le numéro de vol ne peut pas être vide" }
        this.numeroVol = numeroVol
    }

    fun withCompagnie(compagnie: String) = apply {
        require(compagnie.isNotBlank()) { "La compagnie ne peut pas être vide" }
        this.compagnie = compagnie
    }

    fun withDates(depart: LocalDateTime, arrivee: LocalDateTime) = apply {
        require(arrivee.isAfter(depart)) {
            "La date d'arrivée doit être après la date de départ"
        }
        this.dateDepart = depart
        this.dateArrivee = arrivee
    }

    fun withStatut(statut: Statut) = apply { this.statut = statut }

    fun withTypeVol(typeVol: TypeVol) = apply { this.typeVol = typeVol }

    fun withOrigine(origine: AeroportEntity) = apply { this.origine = origine }

    fun withDestination(destination: AeroportEntity) = apply {
        require(this.origine?.id != destination.id) {
            "L'origine et la destination doivent être différentes"
        }
        this.destination = destination
    }

    fun withAvion(avion: AvionEntity?) = apply { this.avionEntity = avion }

    fun withPisteDecollage(piste: PisteEntity?) = apply { this.pisteDecollage = piste }

    fun withPisteAtterissage(piste: PisteEntity?) = apply { this.pisteAtterissage = piste }

    fun build(): VolDomain {
        validate()

        return VolDomain(
            id = id,
            numeroVol = numeroVol,
            compagnie = compagnie,
            dateDepart = dateDepart,
            dateArrivee = dateArrivee,
            statut = statut,
            typeVol = typeVol,
            origine = origine,
            destination = destination,
            avionEntity = avionEntity,
            pisteDecollage = pisteDecollage,
            pisteAtterissage = pisteAtterissage
        )
    }

    private fun validate() {
        require(numeroVol.isNotBlank()) { "Le numéro de vol est obligatoire" }
        require(compagnie.isNotBlank()) { "La compagnie est obligatoire" }
        require(origine != null) { "L'origine est obligatoire" }
        require(destination != null) { "La destination est obligatoire" }
        require(dateArrivee.isAfter(dateDepart)) {
            "La date d'arrivée doit être après la date de départ"
        }
        require(origine?.id != destination?.id) {
            "L'origine et la destination doivent être différentes"
        }
    }
}

