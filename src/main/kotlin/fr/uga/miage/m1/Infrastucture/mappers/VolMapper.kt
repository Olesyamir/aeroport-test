package fr.uga.miage.m1.Infrastucture.mappers

import fr.uga.miage.m1.models.AeroportEntity
import fr.uga.miage.m1.Domain.domains.VolDomain
import fr.uga.miage.m1.Domain.updateRequest.UpdateVolRequest
import fr.uga.miage.m1.Infrastucture.models.AvionEntity
import fr.uga.miage.m1.Infrastucture.models.PisteEntity
import fr.uga.miage.m1.Infrastucture.models.VolEntity
import org.springframework.stereotype.Component

@Component
class
VolMapper {

    fun domainToEntity(
        vol: VolDomain
    ): VolEntity =
        VolEntity(
            id = vol.id,
            numeroVol = vol.numeroVol,
            compagnie = vol.compagnie,
            origine = vol.origine,
            destination = vol.destination,
            dateDepart = vol.dateDepart,
            dateArrivee = vol.dateArrivee,
            statut = vol.statut,
            typeVol = vol.typeVol,
            avionEntity = vol.avionEntity,
            pisteDecollage = vol.pisteDecollage,
            pisteAtterissage = vol.pisteAtterissage,
        )

    fun toUpdate(
        update: UpdateVolRequest,
        vol: VolEntity,
        avion: AvionEntity? = null,
        pisteDecollage: PisteEntity? = null,
        pisteAtterissage: PisteEntity? = null,
        origine: AeroportEntity? = null,
        destination: AeroportEntity? = null
    ) {
        update.numeroVol?.let { vol.numeroVol = it }
        update.compagnie?.let { vol.compagnie = it }
        update.typeVol?.let { vol.typeVol = it }

        vol.dateDepart = update.dateDepart
        vol.dateArrivee = update.dateArrivee
        vol.statut = update.statut

        avion?.let { vol.avionEntity = it }
        pisteDecollage?.let { vol.pisteDecollage = it }
        pisteAtterissage?.let { vol.pisteAtterissage = it }
        origine?.let { vol.origine = it }
        destination?.let { vol.destination = it }
    }

    fun entityToDomain(
        vol: VolEntity,
    ): VolDomain =

        VolDomain(
            id = vol.id,
            numeroVol = vol.numeroVol,
            compagnie = vol.compagnie,
            origine = vol.origine,
            destination = vol.destination,
            dateDepart = vol.dateDepart,
            dateArrivee = vol.dateArrivee,
            statut = vol.statut,
            typeVol = vol.typeVol,
            avionEntity = vol.avionEntity,
            pisteDecollage = vol.pisteDecollage,
            pisteAtterissage = vol.pisteAtterissage
        )

}
