package fr.uga.miage.m1.repository

import fr.uga.miage.m1.models.AeroportEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AeroportRepository : JpaRepository<AeroportEntity, Long> {
    fun findByCodeIATA(codeIATA: String): AeroportEntity?
}