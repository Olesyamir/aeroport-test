package fr.uga.miage.m1.Application.services

import fr.uga.miage.m1.Infrastucture.adapters.PlannningPistesAdapter
import fr.uga.miage.m1.Infrastucture.adapters.VolAdapter
import fr.uga.miage.m1.Domain.enums.Usage
import fr.uga.miage.m1.Application.mapperDomain.PlanningPistesMapperDomain
import fr.uga.miage.m1.Infrastucture.mappers.PlanningPistesMapper
import fr.uga.miage.m1.Infrastucture.mappers.VolMapper
import fr.uga.miage.m1.Infrastucture.models.VolEntity
import fr.uga.miage.m1.Application.responseDTO.PlanningPistesResponseDTO
import fr.uga.miage.m1.Application.requestDTO.PlanningPistesRequestDTO
import fr.uga.miage.m1.Domain.updateRequest.UpdatePlanningPistesRequest
import org.springframework.stereotype.Service


@Service
class PlanningPistesService(
    private val planningPistesMapper: PlanningPistesMapper,
    private val planningPistesMapperDomain: PlanningPistesMapperDomain,
    private val plannningPistesAdapter: PlannningPistesAdapter,
    private val volAdapter: VolAdapter,
    private val volMapper: VolMapper,
) {

    fun getAllPlanningPistes(): Collection<PlanningPistesResponseDTO> =

        plannningPistesAdapter.getAllPlanningPistes()
            .map { planningPistesMapperDomain.domainToResponse(it) }

    fun getPlanningPisteById(id: Long): PlanningPistesResponseDTO =
        plannningPistesAdapter.getPlanningPistesById(id)
            .let { planningPistesMapperDomain.domainToResponse(it) }


    fun createPlanningPiste(request: PlanningPistesRequestDTO): PlanningPistesResponseDTO {
        val volEntities: MutableList<VolEntity> =
            (request.volIds ?: emptyList<Long>()).map { volId ->
                val volDomain = volAdapter.getVolById(volId)
                volMapper.domainToEntity(volDomain)
            }.toMutableList()

        val (volsDepart, volsArrivee) =
            if (request.usage == Usage.DECOLLAGE)
                Pair(volEntities, mutableListOf<VolEntity>())
            else
                Pair(mutableListOf<VolEntity>(), volEntities)

        val planningDomain = planningPistesMapperDomain.requestToDomain(
            request,
            volsDepart = volsDepart,
            volsArrivee = volsArrivee,
            piste = null,
        )
       val planningEntity = planningPistesMapper.domainToEntity(planningDomain)

        volsDepart.forEach { it.planningPistesEntity = planningEntity }
        volsArrivee.forEach { it.planningPistesEntity = planningEntity }

        val savedPlanningDomain = plannningPistesAdapter.savePlanningPistes(planningDomain)

        return planningPistesMapperDomain.domainToResponse(savedPlanningDomain)

    }

    fun deletePlanningPiste(id: Long) {
        val planningDomain = plannningPistesAdapter.getPlanningPistesById(id)
        val planningEntity = planningPistesMapper.domainToEntity(planningDomain)

        // datache des vols associés
        (planningEntity.volsDepart + planningEntity.volsArrivee).forEach { vol ->
            vol.planningPistesEntity = null
            volAdapter.saveVol(vol)
        }

        plannningPistesAdapter.deletePlanningPistesById(planningDomain.id!!)
    }

    fun updatePlanningPisteById(id: Long, update : UpdatePlanningPistesRequest) : PlanningPistesResponseDTO {
        val planningDomain = plannningPistesAdapter.getPlanningPistesById(id)
        val planningEntity = planningPistesMapper.domainToEntity(planningDomain)

        update.volsId?.let { volsIds ->
            val volEntities =
                volsIds.map { volId ->
                    val volDomain = volAdapter.getVolById(volId)
                    volMapper.domainToEntity(volDomain)
                }

            planningEntity.volsDepart.clear()
            planningEntity.volsArrivee.clear()

            if (update.usage == Usage.DECOLLAGE) {
                planningEntity.volsDepart.addAll(volEntities)
            } else {
                planningEntity.volsArrivee.addAll(volEntities)
            }

            volEntities.forEach { vol ->
                vol.planningPistesEntity = planningEntity
                volAdapter.saveVol(vol)
            }
        }

        planningPistesMapper.toUpdate(update, planningEntity)
        val updatedDomain = planningPistesMapper.entityToDomain(planningEntity)
        val savedPlanningDomain = plannningPistesAdapter.savePlanningPistes(updatedDomain)

        return planningPistesMapperDomain.domainToResponse(savedPlanningDomain)
    }











}
