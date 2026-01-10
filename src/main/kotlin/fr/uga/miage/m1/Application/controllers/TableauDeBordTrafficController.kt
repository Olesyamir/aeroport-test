package fr.uga.miage.m1.Application.controllers

import fr.uga.miage.m1.Application.responseDTO.HistoriqueResponseDTO
import fr.uga.miage.m1.Application.responseDTO.PlanningPistesResponseDTO
import fr.uga.miage.m1.Domain.enums.Statut
import fr.uga.miage.m1.endpoints.TableauDeBordTrafficEndpoint
import fr.uga.miage.m1.reponses.TableauDeBordTrafficDTO
import fr.uga.miage.m1.reponses.VolTrafficDTO
import fr.uga.miage.m1.services.TableauDeBordTrafficService
import org.springframework.web.bind.annotation.RestController

@RestController
class TableauDeBordTrafficController(
    private val tableauDeBordTrafficService: TableauDeBordTrafficService
) : TableauDeBordTrafficEndpoint {

    override fun getTableauDeBord(aeroportId: Long): TableauDeBordTrafficDTO {
        return tableauDeBordTrafficService.getTableauDeBord(aeroportId)
    }

    override fun getVolsDepart(aeroportId: Long): Collection<VolTrafficDTO> {
        return tableauDeBordTrafficService.getVolsDepart(aeroportId)
    }

    override fun getVolsArrivee(aeroportId: Long): Collection<VolTrafficDTO> {
        return tableauDeBordTrafficService.getVolsArrivee(aeroportId)
    }

    override fun getVolsParStatut(aeroportId: Long, statut: Statut): Collection<VolTrafficDTO> {
        return tableauDeBordTrafficService.getVolsParStatut(aeroportId, statut)
    }

    override fun getVolsEnCours(aeroportId: Long): Collection<VolTrafficDTO> {
        return tableauDeBordTrafficService.getVolsEnCours(aeroportId)
    }

    override fun getHistoriqueVol(volId: Long): HistoriqueResponseDTO? {
        return tableauDeBordTrafficService.getHistoriqueVol(volId)
    }

    override fun getHistoriquesAeroport(aeroportId: Long): Collection<HistoriqueResponseDTO> {
        return tableauDeBordTrafficService.getHistoriquesAeroport(aeroportId)
    }

    override fun getPlanningsPistesAeroport(aeroportId: Long): Collection<PlanningPistesResponseDTO> {
        return tableauDeBordTrafficService.getPlanningsPistesAeroport(aeroportId)
    }

    override fun getPlanningPiste(pisteId: Long): Collection<PlanningPistesResponseDTO> {
        return tableauDeBordTrafficService.getPlanningPiste(pisteId)
    }

    override fun getVolsArriveeBja(): Collection<VolTrafficDTO> {
        return tableauDeBordTrafficService.getVolsArriveeBja()
    }

    override fun getVolsDepartBja(): Collection<VolTrafficDTO> {
        return tableauDeBordTrafficService.getVolsDepartBja()
    }
}
