package fr.uga.miage.m1.mapperDomain

import fr.uga.miage.m1.Infrastucture.models.VolEntity
import fr.uga.miage.m1.domains.AeroportDomain

import fr.uga.miage.m1.reponses.AeroportResponseDTO
import fr.uga.miage.m1.request.AeroportRequestDTO
import org.springframework.stereotype.Component

@Component
class AeroportMapperDomain {

    fun requestToDomain(
        request: AeroportRequestDTO,
        volsDepart: MutableList<VolEntity> = mutableListOf(),
        volsArrivee: MutableList<VolEntity> = mutableListOf()
    ): AeroportDomain =
        AeroportDomain(
            id = null,
            nom = request.nom,
            ville = request.ville,
            pays = request.pays,
            codeIATA = request.codeIATA,
            volsDepart = volsDepart,
            volsArrivee = volsArrivee
        )

    fun domainToResponse(domain: AeroportDomain): AeroportResponseDTO =
        AeroportResponseDTO(
            id = domain.id!!,
            nom = domain.nom,
            ville = domain.ville,
            pays = domain.pays,
            codeIATA = domain.codeIATA,
            volsDepartIds = domain.volsDepart.mapNotNull { it.id },
            volsArriveeIds = domain.volsArrivee.mapNotNull { it.id }
        )
}
