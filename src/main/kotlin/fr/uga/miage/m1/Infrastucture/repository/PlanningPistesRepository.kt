package fr.uga.miage.m1.Infrastucture.repository

import fr.uga.miage.m1.Infrastucture.models.PlanningPistesEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PlanningPistesRepository : JpaRepository< PlanningPistesEntity, Long >{
    fun findAllByPisteId( pisteId : Long) : Collection<PlanningPistesEntity>

}