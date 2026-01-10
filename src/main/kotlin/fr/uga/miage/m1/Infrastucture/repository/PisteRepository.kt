package fr.uga.miage.m1.Infrastucture.repository

import fr.uga.miage.m1.Infrastucture.models.PisteEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface PisteRepository : JpaRepository<PisteEntity, Long> {

}