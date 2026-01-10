package fr.uga.miage.m1.Application.controllers

import fr.uga.miage.m1.Application.requestDTO.PisteRequestDTO
import fr.uga.miage.m1.Domain.updateRequest.UpdatePisteRequest
import fr.uga.miage.m1.Application.endpoints.PisteEndpoint
import fr.uga.miage.m1.Application.responseDTO.PisteResponseDTO
import fr.uga.miage.m1.Application.services.PisteService
import org.springframework.web.bind.annotation.RestController

@RestController
class PisteController(
    private val pisteService: PisteService
) : PisteEndpoint{

    override fun getAllPiste() : Collection<PisteResponseDTO>{
        return pisteService.getAllPiste()
    }

    override fun getPisteById(id: Long): PisteResponseDTO {
        return pisteService.getPisteById(id)
    }

    override fun createPiste(pisteRequestDTO: PisteRequestDTO) : PisteResponseDTO {
        return pisteService.createPiste(pisteRequestDTO )
    }

    override fun updateEtatPiste(id:Long, updatePisteRequest: UpdatePisteRequest) : PisteResponseDTO {
        return pisteService.updatePiste(id,  updatePisteRequest)
    }

    override fun deletePiste(id:Long){
        pisteService.deletePiste(id)
    }

}