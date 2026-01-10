package fr.uga.miage.m1.Infrastucture.repository

import fr.uga.miage.m1.Infrastucture.models.HistoriqueEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface HistoriqueRepository : JpaRepository<HistoriqueEntity, Long> {

    fun findByIdVol (id : Long) : HistoriqueEntity
}