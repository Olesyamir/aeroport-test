package fr.uga.miage.m1.Infrastucture.repository

import fr.uga.miage.m1.Infrastucture.models.HangarEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface HangarRepository : JpaRepository<HangarEntity, Long> {
}