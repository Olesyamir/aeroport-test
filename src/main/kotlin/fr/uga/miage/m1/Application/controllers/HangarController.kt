package fr.uga.miage.m1.Application.controllers

import fr.uga.miage.m1.Application.requestDTO.HangarRequestDTO
import fr.uga.miage.m1.Domain.updateRequest.UpdateHangarRequest
import fr.uga.miage.m1.Application.endpoints.HangarEndpoint
import fr.uga.miage.m1.Application.responseDTO.HangarResponseDTO
import fr.uga.miage.m1.Application.services.HangarService
import org.springframework.web.bind.annotation.RestController

@RestController
class HangarController (
    val hangarService: HangarService
)  : HangarEndpoint{

    override fun getHangarById(id: Long): HangarResponseDTO {
        return hangarService.getHangarById(id)
    }

    override fun getAllHangars() : Collection<HangarResponseDTO>{
        return hangarService.getAllHangars()
    }

    override fun createHangar(
        request: HangarRequestDTO
    ) : HangarResponseDTO {
        return hangarService.createHangar(request )
    }

    override fun updateHangar(
        id : Long,
        updateRequest: UpdateHangarRequest
    ): HangarResponseDTO{
        return hangarService.updateHangar(id, updateRequest)
    }

    override fun deleteHangar(id: Long){
        hangarService.deleteHangar(id)
    }

}