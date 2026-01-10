package fr.uga.miage.m1.Infrastucture.repository

import fr.uga.miage.m1.Domain.enums.Statut
import fr.uga.miage.m1.Infrastucture.models.AvionEntity
import fr.uga.miage.m1.Infrastucture.models.VolEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface VolRepository : JpaRepository<VolEntity, Long>  {

    fun findByNumeroVol(numVol: String): VolEntity?

    fun findAllByStatut( statut : Statut): Collection<VolEntity>

    fun findStatutById(id : Long) : Statut?

    fun findAllByAvionEntity (avion : AvionEntity) : Collection<VolEntity>

    // fun findByPisteDecollageIdOrPisteAtterissage(pisteDecollageId: Long, pisteAtterissageId: Long) : Collection<VolEntity>
}