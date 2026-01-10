package fr.uga.miage.m1.factory

import fr.uga.miage.m1.adapters.AeroportAdapter
import fr.uga.miage.m1.domains.AeroportDomain
import fr.uga.miage.m1.mapperDomain.AeroportMapperDomain
import fr.uga.miage.m1.mappers.AeroportMapper
import fr.uga.miage.m1.request.AeroportRequestDTO
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException

@Component
class AeroportFactory(
    private val aeroportAdapter: AeroportAdapter,
    private val aeroportMapper: AeroportMapper,
    private val aeroportMapperDomain: AeroportMapperDomain
) {

    fun create(request: AeroportRequestDTO): AeroportDomain {
        aeroportAdapter.searchByCodeIATA(request.codeIATA)?.let {
            throw ResponseStatusException(
                HttpStatus.CONFLICT,
                "Un aéroport avec le code IATA ${request.codeIATA} existe déjà"
            )
        }

        val aeroportDomain = aeroportMapperDomain.requestToDomain(request)

        return aeroportAdapter.saveAeroport(aeroportDomain)
    }
}