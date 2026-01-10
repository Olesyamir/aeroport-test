package fr.uga.miage.m1.Domain.Ports

import fr.uga.miage.m1.Domain.domains.PisteDomain

interface PistePort {
    fun getPisteById(id: Long): PisteDomain
    fun getAllPiste() : Collection<PisteDomain>
    fun deletePiste(id : Long)
    fun savePiste(piste: PisteDomain) : PisteDomain
}