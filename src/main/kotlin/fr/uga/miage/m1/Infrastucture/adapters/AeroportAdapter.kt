package fr.uga.miage.m1.adapters

import fr.uga.miage.m1.domains.AeroportDomain
import fr.uga.miage.m1.mappers.AeroportMapper
import fr.uga.miage.m1.Ports.AeroportPort
import fr.uga.miage.m1.repository.AeroportRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException

@Component
class AeroportAdapter(
    private val aeroportRepository: AeroportRepository,
    private val aeroportMapper: AeroportMapper
) : AeroportPort {

    override fun getAeroportById(id: Long): AeroportDomain {
        return aeroportRepository.findByIdOrNull(id)
            ?.let { aeroportMapper.entityToDomain(it) }
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Aéroport with id $id not found")
    }

    override fun getAllAeroports(): Collection<AeroportDomain> {
        return aeroportRepository.findAll()
            .map { aeroportMapper.entityToDomain(it) }
    }

    override fun deleteAeroportById(id: Long) {
        val aeroport = aeroportRepository.findByIdOrNull(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Aéroport with id $id not found")
        aeroportRepository.delete(aeroport)
    }

    override fun searchByCodeIATA(codeIATA: String): AeroportDomain? {
        return aeroportRepository.findByCodeIATA(codeIATA)
            ?.let { aeroportMapper.entityToDomain(it) }
    }

    override fun saveAeroport(aeroport: AeroportDomain): AeroportDomain {
        return aeroportRepository.save(aeroportMapper.domainToEntity(aeroport))
            .let { aeroportMapper.entityToDomain(it) }
    }
}