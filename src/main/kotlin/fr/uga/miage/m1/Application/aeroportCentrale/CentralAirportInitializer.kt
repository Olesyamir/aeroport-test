package fr.uga.miage.m1.Application.aeroportCentrale

import fr.uga.miage.m1.Ports.AeroportPort
import fr.uga.miage.m1.domains.AeroportDomain
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component

/**
 * Initialise l'aéroport central au démarrage de l'application.
 * Si l'aéroport n'existe pas,
 * il est recréé avant que l'application ne commence à répondre.
 */
@Component
class CentralAirportInitializer(
    private val aeroportPort: AeroportPort
) : ApplicationRunner {

    override fun run(args: ApplicationArguments?) {
        val existing = aeroportPort.searchByCodeIATA(CentralAirport.CODE_IATA)
        if (existing == null) {
            val central = AeroportDomain(
                id = null,
                nom = CentralAirport.NOM,
                ville = CentralAirport.VILLE,
                pays = CentralAirport.PAYS,
                codeIATA = CentralAirport.CODE_IATA
            )
            aeroportPort.saveAeroport(central)
        }

    }
}

