package fr.uga.miage.m1.Application.controllers

import fr.uga.miage.m1.Domain.updateRequest.UpdateVolRequest
import fr.uga.miage.m1.Application.endpoints.VolEndpoint
import fr.uga.miage.m1.Domain.enums.Statut
import fr.uga.miage.m1.Application.responseDTO.VolResponseDTO
import fr.uga.miage.m1.Application.requestDTO.VolRequestDTO
import fr.uga.miage.m1.Application.services.VolService

import org.springframework.web.bind.annotation.RestController


@RestController
class VolController(
    private val volService : VolService,

) : VolEndpoint {

    override fun getVolById(id: Long): VolResponseDTO {
        return volService.getVolById(id)
    }

    override fun getAllVols(): Collection<VolResponseDTO> {
        return volService.getAllVols()
    }

    override fun deleteVolById(id: Long) {
        volService.deleteVolvById(id)
    }

    override fun createVol(entity : VolRequestDTO) : VolResponseDTO =
        volService.createVol(entity)

    override fun searchNum(numeroVol: String): VolResponseDTO? {
        return volService.searchByNumVol(numeroVol)
    }

    override fun updateVolById(id: Long, updateVol: UpdateVolRequest): VolResponseDTO {
        return volService.updateVol(id, updateVol)
    }

    override fun listesVolParStatut(statut: Statut): Collection<VolResponseDTO> =
        volService.listesVolParStatut(statut)

    override fun findStatutById(id: Long): Statut? {
        return volService.getStatutById(id)
    }


}
