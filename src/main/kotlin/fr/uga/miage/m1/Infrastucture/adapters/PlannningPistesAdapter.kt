package fr.uga.miage.m1.Infrastucture.adapters

import fr.uga.miage.m1.Domain.Ports.PlanningPistesPort
import fr.uga.miage.m1.Domain.domains.PlanningPistesDomain
import fr.uga.miage.m1.Infrastucture.mappers.PlanningPistesMapper
import fr.uga.miage.m1.Infrastucture.repository.PlanningPistesRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException


@Component
class PlannningPistesAdapter(
    private val planningPistesRepo: PlanningPistesRepository,
    private val planningPistesMapper: PlanningPistesMapper
): PlanningPistesPort{
    override fun getPlanningPistesById( id : Long): PlanningPistesDomain {
        val entity = planningPistesRepo.findByIdOrNull(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Planning non trouvé")

        return planningPistesMapper.entityToDomain(entity)
    }

    override fun getAllPlanningPistes(): Collection<PlanningPistesDomain> {
        return planningPistesRepo.findAll()
            .map { planningPistesMapper.entityToDomain(it) }
    }

override fun deletePlanningPistesById(id: Long) {
        planningPistesRepo.deleteById(id)
    }

    override fun getPlanningPisteByPisteId(pisteId : Long) : Collection<PlanningPistesDomain> {
        return planningPistesRepo.findAllByPisteId(pisteId)
            .map { planningPistesMapper.entityToDomain(it) }
    }

    override fun savePlanningPistes(planningPistes : PlanningPistesDomain) : PlanningPistesDomain{
        return planningPistesRepo.save(planningPistesMapper.domainToEntity(planningPistes))
            .let { planningPistesMapper.entityToDomain(it) }
    }






}