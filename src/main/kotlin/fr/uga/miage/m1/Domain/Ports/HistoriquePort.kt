package fr.uga.miage.m1.Domain.Ports

import fr.uga.miage.m1.Domain.domains.HistoriqueDomain

interface HistoriquePort {
    fun getHistoriqueById(id : Long) : HistoriqueDomain
    fun getAllHistorique(): Collection<HistoriqueDomain>
    fun save( historique: HistoriqueDomain): HistoriqueDomain
    fun searchByIDVol( idVol : Long) : HistoriqueDomain?
    fun deleteHistoriqueById(id : Long)
}