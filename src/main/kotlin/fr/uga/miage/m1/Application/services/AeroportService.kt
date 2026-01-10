package fr.uga.miage.m1.services

import fr.uga.miage.m1.Application.aeroportCentrale.CentralAirport
import fr.uga.miage.m1.factory.AeroportFactory
import fr.uga.miage.m1.Ports.AeroportPort
import fr.uga.miage.m1.domains.AeroportDomain
import fr.uga.miage.m1.mapperDomain.AeroportMapperDomain
import fr.uga.miage.m1.mappers.AeroportMapper
import fr.uga.miage.m1.reponses.AeroportResponseDTO
import fr.uga.miage.m1.request.AeroportRequestDTO
import fr.uga.miage.m1.updateRequest.UpdateAeroportRequest
import jakarta.transaction.Transactional
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class AeroportService(
    private val aeroportPort: AeroportPort,
    private val aeroportMapper: AeroportMapper,
    private val aeroportMapperDomain: AeroportMapperDomain,
    private val aeroportFactory: AeroportFactory
) {

    fun getAeroportById(id: Long): AeroportResponseDTO =
        aeroportPort.getAeroportById(id)
            .let { aeroportMapperDomain.domainToResponse(it) }

    fun getAllAeroports(): Collection<AeroportResponseDTO> =
        aeroportPort.getAllAeroports()
            .map { aeroportMapperDomain.domainToResponse(it) }

    @Transactional
    fun createAeroport(request: AeroportRequestDTO): AeroportResponseDTO {
        if (request.codeIATA == CentralAirport.CODE_IATA) {
            throw ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "L'aéroport central ne peut pas être créé via l'API."
            )
        }

        val createdDomain: AeroportDomain = aeroportFactory.create(request)
        return aeroportMapperDomain.domainToResponse(createdDomain)
    }

    @Transactional
    fun updateAeroport(id: Long, updateRequest: UpdateAeroportRequest): AeroportResponseDTO {
        val aeroportDomain = aeroportPort.getAeroportById(id)

        if (aeroportDomain.codeIATA == CentralAirport.CODE_IATA) {
            throw ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "L'aéroport central ne peut pas être modifié."
            )
        }

        val aeroportEntity = aeroportMapper.domainToEntity(aeroportDomain)

        updateRequest.codeIATA?.let { newCodeIATA ->
            aeroportPort.searchByCodeIATA(newCodeIATA)?.let { existingAeroport ->
                if (existingAeroport.id != id) {
                    throw ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Le code IATA $newCodeIATA est déjà utilisé par un autre aéroport"
                    )
                }
            }
        }

        aeroportMapper.toUpdate(updateRequest, aeroportEntity)

        val aeroportToSave: AeroportDomain = aeroportMapper.entityToDomain(aeroportEntity)
        val savedDomain = aeroportPort.saveAeroport(aeroportToSave)
        return aeroportMapperDomain.domainToResponse(savedDomain)
    }

    @Transactional
    fun deleteAeroport(id: Long) {
        val aeroport = aeroportPort.getAeroportById(id)

        if (aeroport.codeIATA == CentralAirport.CODE_IATA) {
            throw ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "L'aéroport central ne peut pas être supprimé."
            )
        }

        if (aeroport.volsDepart.isNotEmpty() || aeroport.volsArrivee.isNotEmpty()) {
            throw ResponseStatusException(
                HttpStatus.CONFLICT,
                "Impossible de supprimer l'aéroport : des vols y sont associés"
            )
        }

        aeroportPort.deleteAeroportById(id)
    }

    fun searchByCodeIATA(codeIATA: String): AeroportResponseDTO? =
        aeroportPort.searchByCodeIATA(codeIATA)
            ?.let { aeroportMapperDomain.domainToResponse(it) }

    fun getVolsDepart(id: Long): Collection<Long> {
        val aeroportDomain = aeroportPort.getAeroportById(id)
        return aeroportDomain.volsDepart.mapNotNull { it.id }
    }

    fun getVolsArrivee(id: Long): Collection<Long> {
        val aeroportDomain = aeroportPort.getAeroportById(id)
        return aeroportDomain.volsArrivee.mapNotNull { it.id }
    }
}

