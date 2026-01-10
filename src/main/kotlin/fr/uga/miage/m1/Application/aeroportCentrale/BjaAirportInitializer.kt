package fr.uga.miage.m1.Application.aeroportCentrale

import fr.uga.miage.m1.Ports.AeroportPort
import fr.uga.miage.m1.domains.AeroportDomain
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component

/**
 * Initialise l'aéroport collaboratif de Béjaïa au démarrage de l'application.
 */
@Component
class BjaAirportInitializer(
    private val aeroportPort: AeroportPort
) : ApplicationRunner {

    override fun run(args: ApplicationArguments?) {
        val existing = aeroportPort.searchByCodeIATA(BjaAirport.CODE_IATA)
        if (existing == null) {
            val bja = AeroportDomain(
                id = null,
                nom = BjaAirport.NOM,
                ville = BjaAirport.VILLE,
                pays = BjaAirport.PAYS,
                codeIATA = BjaAirport.CODE_IATA
            )
            aeroportPort.saveAeroport(bja)
        }
    }
}

