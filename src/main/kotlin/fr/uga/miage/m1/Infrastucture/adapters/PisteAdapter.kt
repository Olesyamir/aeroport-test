package fr.uga.miage.m1.Infrastucture.adapters

import fr.uga.miage.m1.Domain.Ports.PistePort
import fr.uga.miage.m1.Domain.domains.PisteDomain
import fr.uga.miage.m1.Infrastucture.mappers.PisteMapper
import fr.uga.miage.m1.Infrastucture.repository.PisteRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException

@Component
class PisteAdapter(
    private val pisteRepository : PisteRepository,
    private val pisteMapper : PisteMapper,
    ) : PistePort {

    override fun getPisteById(id: Long): PisteDomain =
        pisteRepository.findById(id)
            .orElseThrow {
                ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Piste $id non trouvée"
                )
            }
            .let { pisteMapper.entityToDomain(it) }


    override fun getAllPiste() : Collection<PisteDomain>{
        return pisteRepository.findAll()
            .map { pisteMapper.entityToDomain(it) }
    }

    override fun deletePiste(id : Long){
        pisteRepository.deleteById(id)
    }

    override fun savePiste(piste: PisteDomain) : PisteDomain{
        return pisteRepository.save(pisteMapper.domainToEntity( piste))
            .let { pisteMapper.entityToDomain(it) }
    }
}
