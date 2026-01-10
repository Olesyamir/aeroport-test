package fr.uga.miage.m1.Application.factory

import fr.uga.miage.m1.Infrastucture.adapters.AvionAdapter
import fr.uga.miage.m1.Infrastucture.adapters.HangarAdapter
import fr.uga.miage.m1.Infrastucture.adapters.VolAdapter
import fr.uga.miage.m1.Domain.domains.AvionDomain
import fr.uga.miage.m1.Application.mapperDomain.AvionMapperDomain
import fr.uga.miage.m1.Infrastucture.mappers.AvionMapper
import fr.uga.miage.m1.Infrastucture.mappers.HangarMapper
import fr.uga.miage.m1.Infrastucture.mappers.VolMapper
import fr.uga.miage.m1.Infrastucture.models.VolEntity
import fr.uga.miage.m1.Application.requestDTO.AvionRequestDTO
import org.springframework.stereotype.Component

@Component
class AvionFactory(
    private val hangarAdapter : HangarAdapter,
    private val volAdapter: VolAdapter,
    private val avionMapperDomain: AvionMapperDomain,
    private val avionMapper: AvionMapper,
    private val hangarMapper: HangarMapper,
    private val avionAdapter: AvionAdapter,
    private val volMapper: VolMapper,
) {

    fun create(request: AvionRequestDTO): AvionDomain {

        val numImatExiste = avionAdapter.searchByNumImmatricule(request.numImmatricule)
        require (numImatExiste == null) {
            throw IllegalArgumentException("Un avion avec le numéro ${request.numImmatricule} existe déjà.")
        }


        val hangarDomain = request.hangarId?.let { hangarId ->
            val hangarDomain = hangarAdapter.getHangarById(hangarId)
            require (hangarDomain.avionEntities.size <= hangarDomain.capacite) {
                throw IllegalArgumentException(" le hangar ${hangarDomain.id} a atteint sa capacité max, il est plein ")
            }
            hangarDomain
        }
        val hangarEntity = hangarDomain?.let { hangarMapper.domainToEntity(it) }


        val vols: Collection<VolEntity> = (request.volIds ?: emptyList()).map { volId ->
            val volDomain = volAdapter.getVolById(volId)
            require (volDomain.avionEntity == null) {
                throw IllegalArgumentException("Le vol ${volDomain.id} est déjà affecté à l'avion${volDomain.avionEntity?.id}.")
            }
            volMapper.domainToEntity(volDomain)
        }.toMutableList()



        val avionDomain = avionMapperDomain.requestToDomain(request, hangarEntity, vols.toMutableList())

        val saveAvionDomain = avionAdapter.saveAvion(avionDomain)
        val saveAvionEntity = avionMapper.domainToEntity(saveAvionDomain)
        vols.forEach { vol ->
            vol.avionEntity = saveAvionEntity
            volAdapter.saveVol(vol)
        }

        return saveAvionDomain
    }
}
