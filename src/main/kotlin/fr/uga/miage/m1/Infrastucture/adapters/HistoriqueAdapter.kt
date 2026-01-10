package fr.uga.miage.m1.Infrastucture.adapters

import fr.uga.miage.m1.Domain.Ports.HistoriquePort
import fr.uga.miage.m1.Domain.domains.HistoriqueDomain
import fr.uga.miage.m1.Infrastucture.mappers.HistoriqueMapper
import fr.uga.miage.m1.Infrastucture.repository.HistoriqueRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException

@Component
class HistoriqueAdapter(
    private val historiqueRepository: HistoriqueRepository,
    private val historiqueMapper: HistoriqueMapper,
) : HistoriquePort{

    override fun    getHistoriqueById(id : Long) : HistoriqueDomain{
        return historiqueRepository.findById(id)
            .orElseThrow {
                ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Historique not found"
                )
            }
            .let { historiqueMapper.entityToDomain(it) }
    }


    override fun getAllHistorique(): Collection<HistoriqueDomain> {
        return historiqueRepository.findAll()
            .map { historiqueMapper.entityToDomain(it) }
    }

    override fun save(historique: HistoriqueDomain): HistoriqueDomain {
        return historiqueRepository.save(historiqueMapper.domainToEntity(historique))
            .let { historiqueMapper.entityToDomain(it) }
    }

    override fun searchByIDVol(idVol : Long) : HistoriqueDomain? {
        return historiqueRepository.findByIdVol(idVol)
            .let { historiqueMapper.entityToDomain(it) }
    }

    override fun deleteHistoriqueById(id : Long) {
        historiqueRepository.deleteById(id)
    }
}