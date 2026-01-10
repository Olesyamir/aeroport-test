package fr.uga.miage.m1.Application.controllers

import fr.uga.miage.m1.Application.endpoints.HistoriqueEndpoint
import fr.uga.miage.m1.Application.responseDTO.HistoriqueResponseDTO
import fr.uga.miage.m1.Application.requestDTO.HistoriqueRequestDTO
import fr.uga.miage.m1.Application.services.HistoriqueService
import org.springframework.web.bind.annotation.RestController

@RestController
class HistoriqueController(
    private val historiqueService: HistoriqueService,
)  : HistoriqueEndpoint{

    override fun getHistoriqueById(id : Long) : HistoriqueResponseDTO =
        historiqueService.getHistoriqueById(id)

    override fun createHistorique(createHistorique: HistoriqueRequestDTO) : HistoriqueResponseDTO =
        historiqueService.createHistorique(createHistorique)

    override fun getAllHistorique(): Collection<HistoriqueResponseDTO> =
        historiqueService.getAllHistorique()

    override fun searchByIDVol(idVol : Long) : HistoriqueResponseDTO =
        historiqueService.searchByIDVol(idVol)

    override fun deleteHistoriqueById(id: Long) {
       historiqueService.deleteHistoriqueById(id)
    }



}