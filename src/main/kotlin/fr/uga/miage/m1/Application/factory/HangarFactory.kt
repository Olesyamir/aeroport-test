package fr.uga.miage.m1.Application.factory

import fr.uga.miage.m1.Application.mapperDomain.HangarMapperDomain
import fr.uga.miage.m1.Application.responseDTO.HangarResponseDTO
import fr.uga.miage.m1.Application.requestDTO.HangarRequestDTO
import fr.uga.miage.m1.Infrastucture.adapters.AvionAdapter
import fr.uga.miage.m1.Infrastucture.adapters.HangarAdapter
import fr.uga.miage.m1.Infrastucture.mappers.AvionMapper
import fr.uga.miage.m1.Infrastucture.models.AvionEntity
import org.springframework.stereotype.Component

@Component
class HangarFactory(
    private val hangarAdapter: HangarAdapter,
    private val hangarMapperDomain: HangarMapperDomain,
    private val avionAdapter: AvionAdapter,
    private val avionMapper: AvionMapper
) {

    fun create(request: HangarRequestDTO): HangarResponseDTO {
        val avions: Collection<AvionEntity> =
            (request.avionIds ?: emptyList())
                .map { id ->
                    val avionDomain = avionAdapter.getAvionById(id)
                    require(avionDomain.hangarEntity == null) {
                        "L'avion ${avionDomain.id} est déjà dans le hangar ${avionDomain.hangarEntity?.id}."
                    }
                    avionMapper.domainToEntity(avionDomain)
                }
                .toMutableList()

        val hangarDomain = hangarMapperDomain.requestToDomain(
            request,
            avions
        )

        val savedHangarDomain = hangarAdapter.saveHangar(hangarDomain)
        return hangarMapperDomain.domainToResponse(savedHangarDomain)
    }
}
