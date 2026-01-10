package fr.uga.miage.m1.Application.services

import fr.uga.miage.m1.Application.requestDTO.AvionRequestDTO
import fr.uga.miage.m1.Domain.updateRequest.UpdateAvionRequest
import fr.uga.miage.m1.Infrastucture.adapters.AvionAdapter
import fr.uga.miage.m1.Infrastucture.adapters.HangarAdapter
import fr.uga.miage.m1.Infrastucture.adapters.VolAdapter
import fr.uga.miage.m1.Application.factory.AvionFactory
import fr.uga.miage.m1.Application.mapperDomain.AvionMapperDomain
import fr.uga.miage.m1.Infrastucture.mappers.AvionMapper
import fr.uga.miage.m1.Infrastucture.mappers.HangarMapper
import fr.uga.miage.m1.Infrastucture.mappers.VolMapper
import fr.uga.miage.m1.Infrastucture.models.VolEntity
import fr.uga.miage.m1.Application.responseDTO.AvionResponseDTO
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service


@Service
class AvionService(
    private val avionAdapter: AvionAdapter,
    private val avionMapperDomain: AvionMapperDomain,
    private val hangarAdapter: HangarAdapter,
    private val volAdapter: VolAdapter,
    private val avionMapper: AvionMapper,
    private val volMapper: VolMapper,
    private val hangarMapper: HangarMapper,
    private val avionFactory: AvionFactory
) {

    @Transactional
    fun createAvion(avionRequest: AvionRequestDTO): AvionResponseDTO {
        val existing = avionAdapter.searchByNumImmatricule(avionRequest.numImmatricule)
        if (existing != null) {
            throw IllegalArgumentException("Un avion avec ce numéro d'immatricule existe déjà")
        }

        val avionDomain = avionFactory.create(avionRequest)
        return avionMapperDomain.domainToResponse(avionDomain)
    }

    @Transactional
    fun deleteAvionById(id: Long) {
        val avionDomain = avionAdapter.getAvionById(id)
        val vols = volAdapter.findAllByAvion(avionDomain)
        vols?.forEach { volDomain ->
            volDomain.avionEntity = null
            volAdapter.saveVol(volMapper.domainToEntity(volDomain))
        }
        avionAdapter.deleteAvionById(id)

    }

    fun getAllAvions(): Collection<AvionResponseDTO> =
        (avionAdapter.getAllAvions() ?: emptyList())
            .map {
                avionMapperDomain.domainToResponse(it)
            }

    fun getAvionById(id: Long): AvionResponseDTO =
        avionAdapter.getAvionById(id)
            .let { avionMapperDomain.domainToResponse(it) }


    @Transactional
    fun updateAvion(id: Long, updateAvion: UpdateAvionRequest): AvionResponseDTO {
        val avionDomain = avionAdapter.getAvionById(id)
        val avionEntity = avionMapper.domainToEntity(avionDomain)
        val updateHangar = updateAvion.hangarId?.let { hangarId ->
            updateAvion.hangarId.let { hangarId ->
                val hangarDomain = hangarAdapter.getHangarById(hangarId)
                require(hangarDomain.avionEntities.size < hangarDomain.capacite) {
                    throw IllegalArgumentException("Le hangar a atteint sa capacité max, il est plein.")
                }
                hangarMapper.domainToEntity(hangarDomain)
            }
        }

        val updateVols: Collection<VolEntity>? =
            updateAvion.volsId?.map { volId ->
                val vol = volAdapter.getVolById(volId)
                require(vol.avionEntity == null || vol.avionEntity?.id == avionEntity.id) {
                    throw IllegalArgumentException("Le vol ${vol.id} est déjà affecté à l'avion ${vol.avionEntity?.id}.")
                }
                volMapper.domainToEntity(vol)
            }



        avionMapper.toUpdate(updateAvion, avionEntity, updateHangar, updateVols)

        val avionToSave = avionMapper.entityToDomain(avionEntity)
        val savedDomain = avionAdapter.saveAvion(avionToSave)
        return avionMapperDomain.domainToResponse(savedDomain)
    }


    fun searchByNumImmatricule(numImmatricule: String): AvionResponseDTO? =
        avionAdapter.searchByNumImmatricule(numImmatricule)
            ?.let { avionMapperDomain.domainToResponse(it) }


}
