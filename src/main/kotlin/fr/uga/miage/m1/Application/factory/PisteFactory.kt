package fr.uga.miage.m1.Application.factory

import fr.uga.miage.m1.Infrastucture.adapters.PisteAdapter
import fr.uga.miage.m1.Infrastucture.adapters.VolAdapter
import fr.uga.miage.m1.Domain.domains.PisteDomain
import fr.uga.miage.m1.Application.mapperDomain.PisteMapperDomain
import fr.uga.miage.m1.Infrastucture.mappers.PisteMapper
import fr.uga.miage.m1.Infrastucture.mappers.VolMapper
import fr.uga.miage.m1.Infrastucture.models.PisteEntity
import fr.uga.miage.m1.Application.requestDTO.PisteRequestDTO
import org.springframework.stereotype.Component

@Component
class PisteFactory(
    private val pisteAdapter: PisteAdapter,
    private val pisteMapperDomain: PisteMapperDomain,
    private val pisteMapper: PisteMapper,
    private val volAdapter: VolAdapter,
    private val volMapper: VolMapper
) {

    fun create(request: PisteRequestDTO): PisteDomain {

        val volsDepart =
            (request.volsDepartIds ?: emptyList()).map { id ->
                val volDomain = volAdapter.getVolById(id)
                volMapper.domainToEntity(volDomain)
            }

        val volsArrivee =
            (request.volsArriveeIds ?: emptyList()).map { id ->
                val volDomain = volAdapter.getVolById(id)
                volMapper.domainToEntity(volDomain)
            }

        val pisteDomain = pisteMapperDomain.requestToDomain(
            request,
            volsDepart,
            volsArrivee
        )

        val pisteEntity: PisteEntity = pisteMapper.domainToEntity(pisteDomain)

        val savedPisteDomain = pisteAdapter.savePiste(pisteDomain)

        volsDepart.forEach { vol ->
            vol.pisteDecollage = pisteEntity
            volAdapter.saveVol(vol)
        }
        volsArrivee.forEach { vol ->
            vol.pisteAtterissage = pisteEntity
            volAdapter.saveVol(vol)
        }

        return pisteAdapter.getPisteById(savedPisteDomain.id!!)
    }
}
