package fr.uga.miage.m1.controllers

import fr.uga.miage.m1.Application.aeroportCentrale.CentralAirport
import fr.uga.miage.m1.Domain.enums.Etat
import fr.uga.miage.m1.Domain.enums.Statut
import fr.uga.miage.m1.Infrastucture.adapters.ExternalVolBjaAdapter
import fr.uga.miage.m1.Infrastucture.repository.AvionRepository
import fr.uga.miage.m1.Infrastucture.repository.HangarRepository
import fr.uga.miage.m1.Infrastucture.repository.HistoriqueRepository
import fr.uga.miage.m1.Infrastucture.repository.PisteRepository
import fr.uga.miage.m1.Infrastucture.repository.VolRepository
import fr.uga.miage.m1.repository.AeroportRepository
import fr.uga.miage.m1.models.AeroportEntity
import fr.uga.miage.m1.Application.responseDTO.VolExterneBJAResponseDTO
import fr.uga.miage.m1.utils.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.LocalDateTime
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.Mockito.times
import org.springframework.test.context.bean.override.mockito.MockitoBean

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TableauDeBordTrafficControllerITTest {

    @Autowired
    lateinit var webClient: WebTestClient

    @Autowired
    lateinit var aeroportRepository: AeroportRepository

    @Autowired
    lateinit var volRepository: VolRepository

    @Autowired
    lateinit var avionRepository: AvionRepository

    @Autowired
    lateinit var hangarRepository: HangarRepository

    @Autowired
    lateinit var pisteRepository: PisteRepository

    @Autowired
    lateinit var historiqueRepository: HistoriqueRepository

    @MockitoBean
    lateinit var externalVolBjaAdapter: ExternalVolBjaAdapter

    @BeforeEach
    fun cleanDatabase() {
        historiqueRepository.deleteAll()
        volRepository.deleteAll()
        avionRepository.deleteAll()
        hangarRepository.deleteAll()
        pisteRepository.deleteAll()

        val allAeroports = aeroportRepository.findAll()
        allAeroports.forEach { aeroport ->
            if (aeroport.codeIATA != CentralAirport.CODE_IATA) {
                aeroportRepository.delete(aeroport)
            }
        }

        val aeroportCentral = aeroportRepository.findByCodeIATA(CentralAirport.CODE_IATA)
        if (aeroportCentral == null) {
            val central = AeroportEntity(
                nom = CentralAirport.NOM,
                ville = CentralAirport.VILLE,
                pays = CentralAirport.PAYS,
                codeIATA = CentralAirport.CODE_IATA
            )
            aeroportRepository.save(central)
        }
    }

    @Test
    fun `should get tableau de bord for aeroport`() {
        val aeroportCentral = aeroportRepository.getCentralAirportId()
        val destination = webClient.createAeroport(aeroportRequest(codeIATA = "LYS"))

        webClient.createVol(volRequest("AF-TDB1", origineId = aeroportCentral, destinationId = destination.id ))
        webClient.createVol(volRequest("AF-TDB2", origineId = aeroportCentral, destinationId = destination.id ))

        val tableauDeBord = webClient.getTableauDeBord(aeroportCentral)

        assertNotNull(tableauDeBord)
        assertEquals(aeroportCentral, tableauDeBord.aeroportId)
        assertEquals(CentralAirport.NOM, tableauDeBord.aeroportNom)
        assertEquals(CentralAirport.CODE_IATA, tableauDeBord.aeroportCodeIATA)

        assertNotNull(tableauDeBord.statistiques)
        assertNotNull(tableauDeBord.volsDepart)
        assertNotNull(tableauDeBord.volsArrivee)

        assert(tableauDeBord.volsDepart.size >= 2)
    }

    @Test
    fun `should get vols depart for aeroport`() {
        val aeroportCentral = aeroportRepository.getCentralAirportId()
        val destination1 = webClient.createAeroport(aeroportRequest(nom = "Lyon", codeIATA = "LYS"))
        val destination2 = webClient.createAeroport(aeroportRequest(nom = "Marseille", codeIATA = "MRS"))

        // Créer vols au départ
        webClient.createVol(volRequest("AF-DEP1", origineId = aeroportCentral, destinationId = destination1.id ))
        webClient.createVol(volRequest("AF-DEP2", origineId = aeroportCentral, destinationId = destination2.id ))

        val volsDepart = webClient.getVolsDepartTraffic(aeroportCentral)

        assert(volsDepart.size >= 2) { "Expected at least 2 vols depart, but got ${volsDepart.size}" }

        volsDepart.forEach { vol ->
            assertEquals(aeroportCentral, vol.origineId)
        }
    }

    @Test
    fun `should get vols arrivee for aeroport`() {
        val aeroportCentral = aeroportRepository.getCentralAirportId()
        val autreAeroport = webClient.createAeroport(aeroportRequest(nom = "Nice", codeIATA = "NCE"))


        val destination = webClient.createAeroport(aeroportRequest(nom = "Lyon", codeIATA = "LYS"))
        webClient.createVol(volRequest("AF-ARR1", origineId = aeroportCentral, destinationId = destination.id ))

        val volsArrivee = webClient.getVolsArriveeTraffic(destination.id )

        assert(volsArrivee.size >= 1)
        volsArrivee.forEach { vol ->
            assertEquals(destination.id, vol.destinationId)
        }
    }

    @Test
    fun `should get vols par statut`() {
        val aeroportCentral = aeroportRepository.getCentralAirportId()
        val destination1 = webClient.createAeroport(aeroportRequest(nom = "Lyon", codeIATA = "LYS"))
        val destination2 = webClient.createAeroport(aeroportRequest(nom = "Marseille", codeIATA = "MRS"))
        val destination3 = webClient.createAeroport(aeroportRequest(nom = "Nice", codeIATA = "NCE"))

        webClient.createVol(volRequest("AF-ST1", origineId = aeroportCentral, destinationId = destination1.id, statut = Statut.ENATTENTE))
        webClient.createVol(volRequest("AF-ST2", origineId = aeroportCentral, destinationId = destination2.id, statut = Statut.ENATTENTE))
        webClient.createVol(volRequest("AF-ST3", origineId = aeroportCentral, destinationId = destination3.id, statut = Statut.EMBARQUEMENT))

        val volsEnAttente = webClient.getVolsParStatutTraffic(aeroportCentral, Statut.ENATTENTE)
        val volsEmbarquement = webClient.getVolsParStatutTraffic(aeroportCentral, Statut.EMBARQUEMENT)

        assert(volsEnAttente.size >= 2) { "Expected at least 2 vols ENATTENTE, but got ${volsEnAttente.size}" }
        assert(volsEmbarquement.size >= 1) { "Expected at least 1 vol EMBARQUEMENT, but got ${volsEmbarquement.size}" }

        volsEnAttente.forEach { assertEquals(Statut.ENATTENTE, it.statut) }
        volsEmbarquement.forEach { assertEquals(Statut.EMBARQUEMENT, it.statut) }
    }

    @Test
    fun `should get vols en cours`() {
        val aeroportCentral = aeroportRepository.getCentralAirportId()
        val destination1 = webClient.createAeroport(aeroportRequest(nom = "Lyon", codeIATA = "LYS"))
        val destination2 = webClient.createAeroport(aeroportRequest(nom = "Marseille", codeIATA = "MRS"))

        webClient.createVol(volRequest("AF-EC1", origineId = aeroportCentral, destinationId = destination1.id , statut = Statut.DECOLLE))
        webClient.createVol(volRequest("AF-EC2", origineId = aeroportCentral, destinationId = destination2.id , statut = Statut.ENVOL))

        val volsEnCours = webClient.getVolsEnCoursTraffic(aeroportCentral)

        assert(volsEnCours.size >= 2) { "Expected at least 2 vols en cours, but got ${volsEnCours.size}" }

        volsEnCours.forEach { vol ->
            assert(vol.statut == Statut.DECOLLE || vol.statut == Statut.ENVOL)
        }
    }

    @Test
    fun `should get historique for vol`() {
        val aeroportCentral = aeroportRepository.getCentralAirportId()
        val destination = webClient.createAeroport(aeroportRequest(codeIATA = "LYS"))

        val vol = webClient.createVol(volRequest("AF-HIST", origineId = aeroportCentral, destinationId = destination.id ))

        webClient.createHistorique(historiqueRequest(vol.id!! , Statut.EMBARQUEMENT))

        val historique = webClient.getHistoriqueVolTraffic(vol.id )

        assertNotNull(historique)
        assertEquals(vol.id, historique?.idVol)
        assertEquals(Statut.EMBARQUEMENT, historique?.statut)
    }

    @Test
    fun `should get historiques for aeroport`() {
        val aeroportCentral = aeroportRepository.getCentralAirportId()
        val destination1 = webClient.createAeroport(aeroportRequest(nom = "Lyon", codeIATA = "LYS"))
        val destination2 = webClient.createAeroport(aeroportRequest(nom = "Marseille", codeIATA = "MRS"))

        val vol1 = webClient.createVol(volRequest("AF-H1", origineId = aeroportCentral, destinationId = destination1.id ))
        val vol2 = webClient.createVol(volRequest("AF-H2", origineId = aeroportCentral, destinationId = destination2.id ))

        webClient.createHistorique(historiqueRequest(vol1.id!!, Statut.EMBARQUEMENT))
        webClient.createHistorique(historiqueRequest(vol2.id!! , Statut.DECOLLE))

        val historiques = webClient.getHistoriquesAeroportTraffic(aeroportCentral)

        assert(historiques.size >= 2) { "Expected at least 2 historiques, but got ${historiques.size}" }
    }

    @Test
    fun `should get plannings pistes for aeroport`() {
        val aeroportCentral = aeroportRepository.getCentralAirportId()

        val piste = webClient.createPiste(pisteRequest(longueur = 3000.0, etat = Etat.LIBRE))

        val plannings = webClient.getPlanningsPistesAeroportTraffic(aeroportCentral)

        assertNotNull(plannings)
    }

    @Test
    fun `should get planning for specific piste`() {
        val piste = webClient.createPiste(pisteRequest(longueur = 3500.0, etat = Etat.LIBRE))

        val planning = webClient.getPlanningPisteTraffic(piste.id!! )

        assertNotNull(planning)
    }

    @Test
    fun `should return 404 for non-existent aeroport`() {
        webClient.verifyTableauDeBordNotFound(999999L)
    }

    @Test
    fun `should verify statistiques in tableau de bord`() {
        val aeroportCentral = aeroportRepository.getCentralAirportId()
        val destination1 = webClient.createAeroport(aeroportRequest(nom = "Lyon", codeIATA = "LYS"))
        val destination2 = webClient.createAeroport(aeroportRequest(nom = "Marseille", codeIATA = "MRS"))

        webClient.createVol(volRequest("AF-S1", origineId = aeroportCentral, destinationId = destination1.id, statut = Statut.ENATTENTE))
        webClient.createVol(volRequest("AF-S2", origineId = aeroportCentral, destinationId = destination2.id, statut = Statut.DECOLLE))

        val tableauDeBord = webClient.getTableauDeBord(aeroportCentral)

        val stats = tableauDeBord.statistiques
        assertNotNull(stats)
        assert(stats.totalVolsDepart >= 2)
        assert(stats.volsEnCours >= 1)
        assert(stats.tauxOccupationPistes >= 0.0)
        assert(stats.tauxOccupationHangars >= 0.0)
    }

    @Test
    fun `should verify pistes and hangars counts in tableau de bord`() {
        val aeroportCentral = aeroportRepository.getCentralAirportId()
        val destination = webClient.createAeroport(aeroportRequest(codeIATA = "LYS"))

        webClient.createPiste(pisteRequest(longueur = 3000.0, etat = Etat.LIBRE))
        webClient.createPiste(pisteRequest(longueur = 3500.0, etat = Etat.OCCUPE))

        webClient.createVol(volRequest("AF-PC", origineId = aeroportCentral, destinationId = destination.id ))

        val tableauDeBord = webClient.getTableauDeBord(aeroportCentral)

        assert(tableauDeBord.pistesDisponibles >= 1)
        assert(tableauDeBord.pistesOccupees >= 1)
        assert(tableauDeBord.pistesEnMaintenance >= 0)
    }

    @Test
    fun `should get BJA depart traffic via controller`() {
        val now = LocalDateTime.now()

        val mockVolsDepart = listOf(
            VolExterneBJAResponseDTO(
                numeroVol = "BJA001",
                origine = "Béjaïa",
                destination = "Aéroport Abane Ramdane",
                heureDepart = now,
                heureArrivee = now.plusHours(2),
                statut = "DECOLLE",
                avionImmatriculation = "7T-VJA",
                pisteAssignee = "09L"
            ),
            VolExterneBJAResponseDTO(
                numeroVol = "BJA002",
                origine = "Béjaïa",
                destination = "Paris",
                heureDepart = now.plusHours(1),
                heureArrivee = now.plusHours(3),
                statut = "PROGRAMME",
                avionImmatriculation = "7T-VJB",
                pisteAssignee = null
            )
        )

        `when`(externalVolBjaAdapter.getDeparts()).thenReturn(mockVolsDepart)

        val result = webClient.getVolsDepartBja()

        assertNotNull(result)
        assertEquals(2, result.size)

        val vol1 = result.first()
        assertEquals("BJA001", vol1.numeroVol)
        assertEquals("Béjaïa", vol1.origineNom)
        assertEquals("Aéroport Abane Ramdane", vol1.destinationNom)
        assertEquals(Statut.DECOLLE, vol1.statut)

        verify(externalVolBjaAdapter, times(1)).getDeparts()
    }

    @Test
    fun `should get BJA arrivee traffic via controller`() {
        val now = LocalDateTime.now()

        val mockVolsArrivee = listOf(
            VolExterneBJAResponseDTO(
                numeroVol = "BJA101",
                origine = "Paris",
                destination = "Béjaïa",
                heureDepart = now,
                heureArrivee = now.plusHours(2),
                statut = "EN_VOL",
                avionImmatriculation = "F-HBNA",
                pisteAssignee = "27R"
            ),
            VolExterneBJAResponseDTO(
                numeroVol = "BJA102",
                origine = "Lyon",
                destination = "Béjaïa",
                heureDepart = now.plusHours(1),
                heureArrivee = now.plusHours(3),
                statut = "ATTERI",
                avionImmatriculation = "F-GKXY",
                pisteAssignee = "09L"
            )
        )

        `when`(externalVolBjaAdapter.getArrivees()).thenReturn(mockVolsArrivee)

        val result = webClient.getVolsArriveeBja()

        assertNotNull(result)
        assertEquals(2, result.size)

        val vol1 = result.first()
        assertEquals("BJA101", vol1.numeroVol)
        assertEquals("Paris", vol1.origineNom)
        assertEquals("Béjaïa", vol1.destinationNom)
        assertEquals(Statut.ENVOL, vol1.statut)

        verify(externalVolBjaAdapter, times(1)).getArrivees()
    }
}
