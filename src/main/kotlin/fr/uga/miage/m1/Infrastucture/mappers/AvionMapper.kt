package fr.uga.miage.m1.Infrastucture.mappers

import fr.uga.miage.m1.Domain.domains.AvionDomain
import fr.uga.miage.m1.Domain.updateRequest.UpdateAvionRequest
import fr.uga.miage.m1.Infrastucture.models.AvionEntity
import fr.uga.miage.m1.Infrastucture.models.HangarEntity
import fr.uga.miage.m1.Infrastucture.models.VolEntity
import org.springframework.stereotype.Component

@Component
class AvionMapper {

    fun domainToEntity (
        avion: AvionDomain
    ) : AvionEntity =
        AvionEntity(
            id = avion.id,
            nom = avion.nom,
            numImmatricule = avion.numImmatricule,
            type = avion.type,
            capacite = avion.capacite,
            etat = avion.etat,
            hangarEntity = avion.hangarEntity,
            volEntity = avion.volEntity,
        )

    fun toUpdate(
        update : UpdateAvionRequest,
        avion : AvionEntity,
        hangar: HangarEntity? = null,
        vols : Collection<VolEntity> ?= null

    ){
        update.nom.let { avion.nom = it }
        update.numImmatricule.let { avion.numImmatricule = it }
        update.type.let { avion.type = it }
        update.capacite.let { avion.capacite = it }
        update.etat.let { avion.etat = it }
        if (hangar != null) avion.hangarEntity = hangar

        if (vols != null) {
            avion.volEntity.forEach { it.avionEntity = null }
            avion.volEntity.clear()
            vols.forEach { vol ->
                avion.volEntity.add(vol)
                vol.avionEntity = avion
            }
        }

    }

    fun entityToDomain  (avion : AvionEntity) : AvionDomain =
        AvionDomain(
            id = avion.id,
            nom = avion.nom,
            numImmatricule = avion.numImmatricule,
            type = avion.type,
            capacite = avion.capacite,
            etat = avion.etat,
            hangarEntity = avion.hangarEntity,
            volEntity = avion.volEntity,
        )
}