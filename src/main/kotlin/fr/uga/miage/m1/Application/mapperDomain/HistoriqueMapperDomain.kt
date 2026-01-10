package fr.uga.miage.m1.Application.mapperDomain

import fr.uga.miage.m1.Domain.domains.HistoriqueDomain
import fr.uga.miage.m1.Application.responseDTO.HistoriqueResponseDTO
import fr.uga.miage.m1.Application.requestDTO.HistoriqueRequestDTO
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class HistoriqueMapperDomain {

    fun requestToDomain (
        request : HistoriqueRequestDTO,
        date : LocalDateTime?
    ): HistoriqueDomain =
        HistoriqueDomain(
            id = null,
            statut = request.statut,
            datetime = date,
            idVol = request.idVol,
        )

    fun domainToResponse(
        domain: HistoriqueDomain
    ) : HistoriqueResponseDTO =
        HistoriqueResponseDTO(
        id = domain.id,
        statut = domain.statut,
        datetime = domain.datetime,
        idVol = domain.idVol,
    )

}