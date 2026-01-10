package fr.uga.miage.m1.Application.services

import fr.uga.miage.m1.Infrastucture.adapters.HistoriqueAdapter
import fr.uga.miage.m1.Infrastucture.adapters.VolAdapter
import fr.uga.miage.m1.Application.mapperDomain.HistoriqueMapperDomain
import fr.uga.miage.m1.Infrastucture.mappers.HistoriqueMapper
import fr.uga.miage.m1.Infrastucture.mappers.VolMapper
import fr.uga.miage.m1.Application.responseDTO.HistoriqueResponseDTO
import fr.uga.miage.m1.Application.requestDTO.HistoriqueRequestDTO
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.PathVariable
import java.time.LocalDateTime


@Service
class HistoriqueService(
    private val historiqueMapper: HistoriqueMapper,
    private val historiqueAdapter: HistoriqueAdapter,
    private val historiqueMapperDomain: HistoriqueMapperDomain,
    private val volAdapter: VolAdapter,
    private val volMapper: VolMapper

) {

    fun getHistoriqueById(id : Long) : HistoriqueResponseDTO =
        historiqueAdapter.getHistoriqueById(id)
            .let { historiqueMapperDomain.domainToResponse(it) }


    @Transactional
    fun createHistorique(historiqueRequest: HistoriqueRequestDTO) : HistoriqueResponseDTO {
        val now = LocalDateTime.now()
        val historiqueDomain = historiqueMapperDomain.requestToDomain(historiqueRequest, now)
        val saveHistorique = historiqueAdapter.save(historiqueDomain)
        return historiqueMapperDomain.domainToResponse(saveHistorique)
    }

    fun getAllHistorique(): Collection<HistoriqueResponseDTO> =
        historiqueAdapter.getAllHistorique()
    .map { historiqueMapperDomain.domainToResponse(it) }

    fun searchByIDVol(@PathVariable idVol : Long) : HistoriqueResponseDTO =
        historiqueAdapter.searchByIDVol(idVol)
            ?.let { historiqueMapperDomain.domainToResponse(it) }
            ?: throw NoSuchElementException("Vol $idVol introuvable")


    fun deleteHistoriqueById(@PathVariable id : Long) {
        historiqueAdapter.deleteHistoriqueById(id)
    }


}