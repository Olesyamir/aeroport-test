package fr.uga.miage.m1.Application.controllers

import fr.uga.miage.m1.Application.endpoints.PlanningPistesEndpoint
import fr.uga.miage.m1.Application.responseDTO.PlanningPistesResponseDTO
import fr.uga.miage.m1.Application.requestDTO.PlanningPistesRequestDTO
import fr.uga.miage.m1.Application.services.PlanningPistesService
import fr.uga.miage.m1.Domain.updateRequest.UpdatePlanningPistesRequest
import org.springframework.web.bind.annotation.RestController

@RestController
class PlanningPistesController(
    private val planningPistesService : PlanningPistesService
): PlanningPistesEndpoint{

    override fun getAllPlanningPistes(): Collection<PlanningPistesResponseDTO> {
        return planningPistesService.getAllPlanningPistes()
    }

        override fun getPlanningPisteById(id: Long): PlanningPistesResponseDTO {
        return planningPistesService.getPlanningPisteById(id)
    }

    override fun createPlanningPiste(entity: PlanningPistesRequestDTO): PlanningPistesResponseDTO {
       return planningPistesService.createPlanningPiste(entity)
    }

    override fun deletePlanningPisteById(id: Long) {
        return planningPistesService.deletePlanningPiste(id)
    }


    override fun updatePlanningPisteById(id: Long, update : UpdatePlanningPistesRequest) : PlanningPistesResponseDTO {
        return planningPistesService.updatePlanningPisteById(id, update)
    }

}