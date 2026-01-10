package fr.uga.miage.m1.Infrastucture.mappers

import fr.uga.miage.m1.Domain.domains.PisteDomain
import fr.uga.miage.m1.Domain.updateRequest.UpdatePisteRequest
import fr.uga.miage.m1.Infrastucture.models.PisteEntity
import fr.uga.miage.m1.Infrastucture.models.VolEntity
import org.springframework.stereotype.Component


@Component
class PisteMapper {

    fun domainToEntity(piste : PisteDomain) : PisteEntity =
        PisteEntity(
            id = piste.id,
            longueur = piste.longueur,
            etat = piste.etat,
            volsDepart = piste.volsDepart,
            volsArrivee = piste.volsArrivee
        );

fun toUpdate(
    updateRequest: UpdatePisteRequest,
    pisteEntity: PisteEntity,
    nouveauxVolsDepart: Collection<VolEntity>,
    nouveauxVolsArrivee: Collection<VolEntity>
) {
    // Champs simples
    pisteEntity.longueur = updateRequest.longueur
    pisteEntity.etat = updateRequest.etat

    // Vols de départ
    if (updateRequest.volsDepartIds != null) {
        pisteEntity.volsDepart.clear()
        pisteEntity.volsDepart.addAll(nouveauxVolsDepart)
    }

    // Vols d'arrivée
    if (updateRequest.volsArriveeIds != null) {
        pisteEntity.volsArrivee.clear()
        pisteEntity.volsArrivee.addAll(nouveauxVolsArrivee)
    }
}

    fun entityToDomain(piste : PisteEntity) : PisteDomain =
        PisteDomain(
            id = piste.id,
            longueur = piste.longueur,
            etat = piste.etat,
            volsDepart = piste.volsDepart,
            volsArrivee = piste.volsArrivee
        )

}