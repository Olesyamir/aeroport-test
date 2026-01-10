package fr.uga.miage.m1.Infrastucture.repository

import fr.uga.miage.m1.Infrastucture.models.AvionEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AvionRepository : JpaRepository<AvionEntity, Long>
     {
          fun findByNumImmatriculeOrIdNull(numImmatricule: String): AvionEntity?
}