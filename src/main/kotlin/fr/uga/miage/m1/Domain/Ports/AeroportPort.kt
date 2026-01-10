package fr.uga.miage.m1.Ports

import fr.uga.miage.m1.domains.AeroportDomain

interface AeroportPort {
    fun getAeroportById(id: Long): AeroportDomain
    fun getAllAeroports(): Collection<AeroportDomain>
    fun deleteAeroportById(id: Long)
    fun searchByCodeIATA(codeIATA: String): AeroportDomain?
    fun saveAeroport(aeroport: AeroportDomain): AeroportDomain
}