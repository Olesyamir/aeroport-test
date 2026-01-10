package fr.uga.miage.m1.mappers

import fr.uga.miage.m1.Infrastucture.models.HangarEntity
import fr.uga.miage.m1.Infrastucture.models.PisteEntity
import fr.uga.miage.m1.Infrastucture.models.VolEntity
import fr.uga.miage.m1.domains.AeroportDomain
import fr.uga.miage.m1.updateRequest.UpdateAeroportRequest
import fr.uga.miage.m1.models.AeroportEntity
import org.springframework.stereotype.Component

@Component
class AeroportMapper {

    fun domainToEntity(
        aeroport: AeroportDomain
    ): AeroportEntity =
        AeroportEntity(
            id = aeroport.id,
            nom = aeroport.nom,
            ville = aeroport.ville,
            pays = aeroport.pays,
            codeIATA = aeroport.codeIATA,
            volsDepart = aeroport.volsDepart,
            volsArrivee = aeroport.volsArrivee,
        )

    fun entityToDomain(
        aeroport: AeroportEntity
    ): AeroportDomain =
        AeroportDomain(
            id = aeroport.id,
            nom = aeroport.nom,
            ville = aeroport.ville,
            pays = aeroport.pays,
            codeIATA = aeroport.codeIATA,
            volsDepart = aeroport.volsDepart,
            volsArrivee = aeroport.volsArrivee,
        )

    fun toUpdate(
        update: UpdateAeroportRequest,
        aeroport: AeroportEntity,
        volsDepart: Collection<VolEntity>? = null,
        volsArrivee: Collection<VolEntity>? = null,
        pistes: Collection<PisteEntity>? = null,
        hangars: Collection<HangarEntity>? = null
    ) {
        update.nom?.let { aeroport.nom = it }
        update.ville?.let { aeroport.ville = it }
        update.pays?.let { aeroport.pays = it }
        update.codeIATA?.let { aeroport.codeIATA = it }

        if (volsDepart != null) {
            aeroport.volsDepart.clear()
            aeroport.volsDepart.addAll(volsDepart)
        }

        if (volsArrivee != null) {
            aeroport.volsArrivee.clear()
            aeroport.volsArrivee.addAll(volsArrivee)
        }

    }
}