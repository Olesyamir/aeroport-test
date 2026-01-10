package fr.uga.miage.m1.Application.controllers


import fr.uga.miage.m1.Domain.updateRequest.UpdateAvionRequest
import fr.uga.miage.m1.Application.endpoints.AvionEndpoint
import fr.uga.miage.m1.Application.requestDTO.AvionRequestDTO
import fr.uga.miage.m1.Application.responseDTO.AvionResponseDTO
import fr.uga.miage.m1.Application.services.AvionService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
class AvionController (
    private val avionService : AvionService
) : AvionEndpoint{

    override fun getAllAvions(): Collection<AvionResponseDTO> {
        return avionService.getAllAvions()
    }

    override fun getAvionById(id: Long): AvionResponseDTO? {
        return avionService.getAvionById(id)
    }


    override fun createAvion(createAvion: AvionRequestDTO): AvionResponseDTO  {
        return avionService.createAvion(createAvion)
    }

    override fun deleteAvionById(id: Long) {
        return avionService.deleteAvionById(id)
    }

    override fun updateAvion(id: Long, updateAvion: UpdateAvionRequest): AvionResponseDTO  {
        return avionService.updateAvion(id, updateAvion)
    }

    override fun searchByNumImmatricule(numImmatricule: String): AvionResponseDTO {
        return avionService.searchByNumImmatricule(numImmatricule)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Avion $numImmatricule introuvable")
    }


}
