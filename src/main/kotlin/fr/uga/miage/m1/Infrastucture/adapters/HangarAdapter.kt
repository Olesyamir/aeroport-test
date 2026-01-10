package fr.uga.miage.m1.Infrastucture.adapters

import fr.uga.miage.m1.Application.mapperDomain.HangarMapperDomain
import fr.uga.miage.m1.Domain.Ports.HangarPort
import fr.uga.miage.m1.Domain.domains.HangarDomain
import fr.uga.miage.m1.Infrastucture.mappers.HangarMapper
import fr.uga.miage.m1.Infrastucture.repository.AvionRepository
import fr.uga.miage.m1.Infrastucture.repository.HangarRepository
import fr.uga.miage.m1.repository.AeroportRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException

@Component
class HangarAdapter(
    private val hangarRepository : HangarRepository,
    private val avionRepository : AvionRepository,
    private val hangarMapper: HangarMapper,
    private val hangarMapperDomain: HangarMapperDomain,
    private val aeroportRepository: AeroportRepository
) : HangarPort {

    override fun getHangarById(id:Long): HangarDomain{
        return hangarRepository.findByIdOrNull(id)
            ?.let { hangarMapper.entityToDomain(it) }
            ?:throw ResponseStatusException(HttpStatus.NOT_FOUND, "Hangar id $id not found")
    }

    override fun getAllHangars(): Collection<HangarDomain>{
        return hangarRepository.findAll()
            .map { hangarMapper.entityToDomain(it) }
    }

    override fun deleteHangar(id:Long){
        hangarRepository.deleteById(id)
    }

    override fun saveHangar(hangar: HangarDomain) : HangarDomain{
        return hangarRepository.save(hangarMapper.domainToEntity(hangar))
            .let { hangarMapper.entityToDomain(it) }
    }
}















