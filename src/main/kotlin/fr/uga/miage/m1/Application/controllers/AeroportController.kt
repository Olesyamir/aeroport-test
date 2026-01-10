package fr.uga.miage.m1.controllers

import fr.uga.miage.m1.endpoints.AeroportEndpoint
import fr.uga.miage.m1.reponses.AeroportResponseDTO
import fr.uga.miage.m1.request.AeroportRequestDTO
import fr.uga.miage.m1.services.AeroportService
import fr.uga.miage.m1.updateRequest.UpdateAeroportRequest
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.RestController

@RestController
class AeroportController(
    private val aeroportService: AeroportService
) : AeroportEndpoint {

    override fun getAeroportById(id: Long): AeroportResponseDTO {
        return aeroportService.getAeroportById(id)
    }

    override fun getAllAeroports(): Collection<AeroportResponseDTO> {
        return aeroportService.getAllAeroports()
    }

    override fun createAeroport(@Valid request: AeroportRequestDTO): AeroportResponseDTO {
        return aeroportService.createAeroport(request)
    }

    override fun updateAeroport(
        id: Long,
        updateRequest: UpdateAeroportRequest
    ): AeroportResponseDTO {
        return aeroportService.updateAeroport(id, updateRequest)
    }

    override fun deleteAeroport(id: Long) {
        aeroportService.deleteAeroport(id)
    }

    override fun searchByCodeIATA(codeIATA: String): AeroportResponseDTO? {
        return aeroportService.searchByCodeIATA(codeIATA)
    }

    override fun getVolsDepart(id: Long): Collection<Long> {
        return aeroportService.getVolsDepart(id)
    }

    override fun getVolsArrivee(id: Long): Collection<Long> {
        return aeroportService.getVolsArrivee(id)
    }
}