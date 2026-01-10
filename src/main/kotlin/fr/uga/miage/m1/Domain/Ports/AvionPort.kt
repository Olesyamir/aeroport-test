package fr.uga.miage.m1.Domain.Ports

import fr.uga.miage.m1.Domain.domains.AvionDomain

interface AvionPort {
    fun getAvionById(id : Long) : AvionDomain
    fun getAllAvions() : Collection<AvionDomain>?
    fun deleteAvionById(id: Long)
    fun searchByNumImmatricule(numImmatricule: String) : AvionDomain?
    fun saveAvion(avion: AvionDomain) : AvionDomain?
}