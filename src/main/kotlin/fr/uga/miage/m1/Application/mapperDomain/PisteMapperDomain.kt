package fr.uga.miage.m1.Application.mapperDomain

import fr.uga.miage.m1.Domain.domains.PisteDomain
import fr.uga.miage.m1.Infrastucture.models.VolEntity
import fr.uga.miage.m1.Application.responseDTO.PisteResponseDTO
import fr.uga.miage.m1.Application.requestDTO.PisteRequestDTO
import org.springframework.stereotype.Component

@Component
class PisteMapperDomain {

    fun requestToDomain(
        request: PisteRequestDTO,
//        avions : Collection<AvionEntity>,
        volsDepart : Collection<VolEntity>,
        volsArrivee : Collection<VolEntity>
    ) : PisteDomain =
        PisteDomain(
            id = null,
            longueur = request.longueur,
            etat = request.etat,
//            avionEntities = avions.toMutableList(),
            volsDepart = volsDepart.toMutableList(),
            volsArrivee = volsArrivee.toMutableList()
        )


    fun domainToResponse(
        domain : PisteDomain,
    ) : PisteResponseDTO =
        PisteResponseDTO(
            id = domain.id,
            longueur = domain.longueur,
            etat = domain.etat,
//            avions = domain.avionEntities.mapNotNull { it.id },
            volsDepart = domain.volsDepart.mapNotNull { it.id },
            volsArrivee = domain.volsArrivee.mapNotNull { it.id }
        )

}
