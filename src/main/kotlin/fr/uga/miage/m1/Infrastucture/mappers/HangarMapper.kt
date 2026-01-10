package fr.uga.miage.m1.Infrastucture.mappers

import fr.uga.miage.m1.Domain.domains.HangarDomain
import fr.uga.miage.m1.Domain.updateRequest.UpdateHangarRequest
import fr.uga.miage.m1.Infrastucture.models.AvionEntity
import fr.uga.miage.m1.Infrastucture.models.HangarEntity
import org.springframework.stereotype.Component


@Component
class HangarMapper{

    fun domainToEntity(
        hangar: HangarDomain
    ) : HangarEntity =
        HangarEntity(
            id = hangar.id,
            capacite = hangar.capacite,
            etat = hangar.etat,
            avionEntities = hangar.avionEntities,
        )

    fun toUpdate(
        update: UpdateHangarRequest,
        hangar: HangarEntity,
        avions: Collection<AvionEntity>
    ) {
        update.capacite.let { hangar.capacite = it }
        update.etat.let { hangar.etat = it }

        hangar.avionEntities.forEach { it.hangarEntity = null }
        hangar.avionEntities.clear()
        avions.forEach {
            it.hangarEntity = hangar
            hangar.avionEntities.add(it)
        }
    }


    fun entityToDomain(hangar: HangarEntity) : HangarDomain=
        HangarDomain(
            id = hangar.id,
            capacite = hangar.capacite,
            etat = hangar.etat,
            avionEntities = hangar.avionEntities
        )


}