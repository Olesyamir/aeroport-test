package fr.uga.miage.m1.Infrastucture.adapters

import fr.uga.miage.m1.Domain.Ports.AvionPort
import fr.uga.miage.m1.Domain.domains.AvionDomain
import fr.uga.miage.m1.Infrastucture.mappers.AvionMapper
import fr.uga.miage.m1.Infrastucture.repository.AvionRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException

@Component
class AvionAdapter(
    private val avionRepository: AvionRepository,
    private val avionMapper: AvionMapper
) : AvionPort {

    override fun getAvionById(id : Long) : AvionDomain {
        return avionRepository.findByIdOrNull(id)
            ?.let { avionMapper.entityToDomain(it) }
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Avion with id $id not found")
    }

    override fun getAllAvions() : Collection<AvionDomain>? {
        return avionRepository.findAll()
            .map {avionMapper.entityToDomain(it)  }
    }

    override fun deleteAvionById(id: Long) {
        val avion = avionRepository.findByIdOrNull(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Avion with id $id not found")
        avionRepository.delete(avion)
    }

    override fun searchByNumImmatricule(numImmatricule: String) : AvionDomain?{
        return avionRepository.findByNumImmatriculeOrIdNull(numImmatricule)
        ?.let { avionMapper.entityToDomain(it) }

    }

    override fun saveAvion(avion: AvionDomain) : AvionDomain {
        return avionRepository.save(avionMapper.domainToEntity(avion))
            .let { avionMapper.entityToDomain(it) }
    }

}