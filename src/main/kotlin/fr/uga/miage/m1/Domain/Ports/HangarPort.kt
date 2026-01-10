package fr.uga.miage.m1.Domain.Ports

import fr.uga.miage.m1.Domain.domains.HangarDomain

interface HangarPort {
    fun getHangarById(id:Long): HangarDomain
    fun getAllHangars(): Collection<HangarDomain>
    fun deleteHangar(id:Long)
    fun saveHangar(hangar: HangarDomain) : HangarDomain
}