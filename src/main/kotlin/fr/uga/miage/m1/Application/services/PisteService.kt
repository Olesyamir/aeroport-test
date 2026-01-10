package fr.uga.miage.m1.Application.services

import fr.uga.miage.m1.Application.requestDTO.PisteRequestDTO
import fr.uga.miage.m1.Domain.updateRequest.UpdatePisteRequest
import fr.uga.miage.m1.Infrastucture.adapters.PisteAdapter
import fr.uga.miage.m1.Infrastucture.adapters.VolAdapter
import fr.uga.miage.m1.Application.factory.PisteFactory
import fr.uga.miage.m1.Application.mapperDomain.PisteMapperDomain
import fr.uga.miage.m1.Infrastucture.mappers.PisteMapper
import fr.uga.miage.m1.Infrastucture.mappers.VolMapper
import fr.uga.miage.m1.Application.responseDTO.PisteResponseDTO
import fr.uga.miage.m1.Infrastucture.models.VolEntity
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service



@Service
class PisteService(
    private val pisteAdapter: PisteAdapter,
    private val pisteMapper: PisteMapper,
    private val pisteMapperDomain: PisteMapperDomain,
    private val volAdapter: VolAdapter,
    private val volMapper: VolMapper,
    private val pisteFactory: PisteFactory,
){

    fun getPisteById(id:Long):PisteResponseDTO =
        pisteAdapter.getPisteById(id)
            .let { pisteMapperDomain.domainToResponse(it) }


    fun getAllPiste() : Collection<PisteResponseDTO> =
        pisteAdapter.getAllPiste()
            .map { pisteMapperDomain.domainToResponse(it) }


    @Transactional
    fun createPiste(createPiste: PisteRequestDTO): PisteResponseDTO {
        val pisteDomain = pisteFactory.create(createPiste)
        return pisteMapperDomain.domainToResponse(pisteDomain)
    }


@Transactional
fun updatePiste(id: Long, updatePiste: UpdatePisteRequest): PisteResponseDTO {
    val pisteDomain = pisteAdapter.getPisteById(id)

    val pisteEntity = pisteMapper.domainToEntity(pisteDomain)

    val nouveauxVolsDepart: Collection<VolEntity> = (updatePiste.volsDepartIds ?: emptyList())
        .map { volId ->
            val volDomain = volAdapter.getVolById(volId)
            volMapper.domainToEntity(volDomain)
        }

    val nouveauxVolsArrivee: Collection<VolEntity> = (updatePiste.volsArriveeIds ?: emptyList())
        .map { volId ->
            val volDomain = volAdapter.getVolById(volId)
            volMapper.domainToEntity(volDomain)
        }

    pisteMapper.toUpdate(updatePiste, pisteEntity, nouveauxVolsDepart, nouveauxVolsArrivee)

    val updatedPisteDomain = pisteMapper.entityToDomain(pisteEntity)

    val savedPisteDomain = pisteAdapter.savePiste(updatedPisteDomain)

    return pisteMapperDomain.domainToResponse(savedPisteDomain)
}

    @Transactional
    fun deletePiste(id:Long) {
        val piste = pisteAdapter.getPisteById(id)
        pisteAdapter.deletePiste(piste.id!!)
    }
}
