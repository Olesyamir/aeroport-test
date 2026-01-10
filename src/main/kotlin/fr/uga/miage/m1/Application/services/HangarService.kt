package fr.uga.miage.m1.Application.services

import fr.uga.miage.m1.Application.factory.HangarFactory
import fr.uga.miage.m1.Application.requestDTO.HangarRequestDTO
import fr.uga.miage.m1.Application.responseDTO.HangarResponseDTO
import fr.uga.miage.m1.Domain.updateRequest.UpdateHangarRequest
import fr.uga.miage.m1.Infrastucture.adapters.AvionAdapter
import fr.uga.miage.m1.Infrastucture.adapters.HangarAdapter
import fr.uga.miage.m1.Application.mapperDomain.HangarMapperDomain
import fr.uga.miage.m1.Infrastucture.mappers.AvionMapper
import fr.uga.miage.m1.Infrastucture.mappers.HangarMapper
import fr.uga.miage.m1.Infrastucture.models.AvionEntity
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class HangarService(
    private val hangarAdapter: HangarAdapter,
    private val hangarMapper: HangarMapper,
    private val avionMapper: AvionMapper,
    private val avionAdapter: AvionAdapter,
    private val hangarMapperDomain: HangarMapperDomain,
    private val hangarFactory: HangarFactory
) {

    @Transactional
    fun createHangar(createHangar: HangarRequestDTO): HangarResponseDTO =
        hangarFactory.create(createHangar)

    fun deleteHangar(id: Long) =
        hangarAdapter.deleteHangar(id)

    fun getAllHangars(): Collection<HangarResponseDTO> =
        hangarAdapter.getAllHangars()
            .map { hangarMapperDomain.domainToResponse(it) }

    fun getHangarById(id: Long): HangarResponseDTO =
        hangarAdapter.getHangarById(id)
            .let { hangarMapperDomain.domainToResponse(it) }

    @Transactional
    fun updateHangar(id: Long, updateHangar: UpdateHangarRequest): HangarResponseDTO {
        val hangarDomain = hangarAdapter.getHangarById(id)
        val hangarEntity = hangarMapper.domainToEntity(hangarDomain)


        val updateAvions: Collection<AvionEntity> = (updateHangar.avionIds ?: emptyList())
            .map { avionId ->
                val avionDomain = avionAdapter.getAvionById(avionId)
                avionMapper.domainToEntity(avionDomain)
            }

        hangarMapper.toUpdate(updateHangar, hangarEntity, updateAvions)

        val updatedHangarDomain = hangarMapper.entityToDomain(hangarEntity)

        val savedHangarDomain = hangarAdapter.saveHangar(updatedHangarDomain)
        return hangarMapperDomain.domainToResponse(savedHangarDomain)
    }
}