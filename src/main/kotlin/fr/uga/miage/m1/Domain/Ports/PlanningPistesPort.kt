package fr.uga.miage.m1.Domain.Ports

import fr.uga.miage.m1.Domain.domains.PlanningPistesDomain


interface PlanningPistesPort {
    fun getPlanningPistesById( id : Long): PlanningPistesDomain
    fun getAllPlanningPistes(): Collection<PlanningPistesDomain>
    fun deletePlanningPistesById(id: Long)
    fun getPlanningPisteByPisteId(pisteId : Long) : Collection<PlanningPistesDomain>
    fun savePlanningPistes(planningPistes : PlanningPistesDomain) : PlanningPistesDomain
}