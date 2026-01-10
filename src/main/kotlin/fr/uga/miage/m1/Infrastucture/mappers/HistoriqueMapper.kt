package fr.uga.miage.m1.Infrastucture.mappers

import fr.uga.miage.m1.Domain.domains.HistoriqueDomain
import fr.uga.miage.m1.Infrastucture.models.HistoriqueEntity
import org.springframework.stereotype.Component

@Component
class HistoriqueMapper {

    fun domainToEntity( historique: HistoriqueDomain): HistoriqueEntity=
        HistoriqueEntity(
            id = historique.id,
            statut = historique.statut,
            datetime = historique.datetime,
            idVol = historique.idVol,
        )

    fun entityToDomain(
        historique: HistoriqueEntity
    ) : HistoriqueDomain = HistoriqueDomain(
        id = historique.id,
        statut = historique.statut,
        datetime = historique.datetime,
        idVol = historique.idVol,
    )
}


