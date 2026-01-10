package fr.uga.miage.m1.Domain.Ports

import fr.uga.miage.m1.Domain.domains.AvionDomain
import fr.uga.miage.m1.Domain.domains.VolDomain
import fr.uga.miage.m1.Domain.enums.Statut
import fr.uga.miage.m1.Infrastucture.models.VolEntity

interface VolPort {
    fun getVolById( id : Long) : VolDomain
    fun getAllVols() : Collection<VolDomain>
    fun deleteVolById(id : Long)
    fun saveVol(vol: VolEntity) : VolDomain?
    fun searchByNumVol(numVol: String) : VolDomain?
    fun listesVolParStatut(statut: Statut) : Collection<VolDomain>
    fun getStatutById(id : Long) : Statut?
    fun findAllByAvion(avion : AvionDomain) : Collection<VolDomain>?
}