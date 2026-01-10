package fr.uga.miage.m1.Infrastucture.adapters

import fr.uga.miage.m1.Domain.Ports.VolPort
import fr.uga.miage.m1.Domain.domains.AvionDomain
import fr.uga.miage.m1.Domain.domains.VolDomain
import fr.uga.miage.m1.Domain.enums.Statut
import fr.uga.miage.m1.Infrastucture.mappers.AvionMapper
import fr.uga.miage.m1.Infrastucture.mappers.VolMapper
import fr.uga.miage.m1.Infrastucture.models.VolEntity
import fr.uga.miage.m1.Infrastucture.repository.VolRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException


@Component
class VolAdapter(
    private val volRepository: VolRepository,
    private val volMapper: VolMapper,
    private val avionMapper: AvionMapper,
    ) : VolPort {

    override fun getVolById(id : Long) : VolDomain {
        return volRepository.findById(id)
            .orElseThrow{
                ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Vol $id not found")
            }
            .let { volMapper.entityToDomain(it) }
    }


    override fun getAllVols() : Collection<VolDomain>{
        return volRepository.findAll()
            .map { volMapper.entityToDomain(it) }
    }

    override fun deleteVolById(id : Long){
        volRepository.deleteById(id)
    }

    override fun saveVol(vol: VolEntity) : VolDomain? {
        return volRepository.save(vol)
            .let { volMapper.entityToDomain(it) }
    }


    override fun searchByNumVol(numVol: String) : VolDomain?{
        return volRepository.findByNumeroVol(numVol)
            ?.let { volMapper.entityToDomain(it) }
    }

    override fun listesVolParStatut(statut: Statut) : Collection<VolDomain>{
        return volRepository.findAllByStatut(statut)
            .map { volMapper.entityToDomain(it) }
    }

    override fun getStatutById(id : Long) : Statut? =
        volRepository.findStatutById(id)

    override fun findAllByAvion(avion : AvionDomain) : Collection<VolDomain>? {
        return volRepository.findAllByAvionEntity(avionMapper.domainToEntity(avion))
            .map { volMapper.entityToDomain(it) }
    }
}