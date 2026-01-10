package fr.uga.miage.m1.controllers

import fr.uga.miage.m1.Application.aeroportCentrale.CentralAirport
import fr.uga.miage.m1.Domain.enums.Statut
import fr.uga.miage.m1.Infrastucture.repository.AvionRepository
import fr.uga.miage.m1.Infrastucture.repository.HangarRepository
import fr.uga.miage.m1.Infrastucture.repository.HistoriqueRepository
import fr.uga.miage.m1.Infrastucture.repository.PisteRepository
import fr.uga.miage.m1.Infrastucture.repository.VolRepository
import fr.uga.miage.m1.repository.AeroportRepository
import fr.uga.miage.m1.models.AeroportEntity
import fr.uga.miage.m1.utils.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HistoriqueControllerITTest {

    @Autowired
    lateinit var webClient: WebTestClient

    @Autowired
    lateinit var historiqueRepository: HistoriqueRepository

    @Autowired
    lateinit var volRepository: VolRepository

    @Autowired
    lateinit var avionRepository: AvionRepository

    @Autowired
    lateinit var hangarRepository: HangarRepository

    @Autowired
    lateinit var pisteRepository: PisteRepository

    @Autowired
    lateinit var aeroportRepository: AeroportRepository

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
    fun `should create and retrieve historique`() {
        val aeroportCentral = aeroportRepository.getCentralAirportId()
        val destination = webClient.createAeroport(aeroportRequest(codeIATA = "LYS"))

        val volRequest = volRequest(
            numeroVol = "AF-HIST1",
            origineId = aeroportCentral,
            destinationId = destination.id,
            statut = Statut.ENATTENTE
        )
        val createdVol = webClient.createVol(volRequest)

        // Créer historique
        val histRequest = historiqueRequest(
            idVol = createdVol.id!!,
            statut = Statut.EMBARQUEMENT
        )
        val createdHistorique = webClient.createHistorique(histRequest)

        // Vérifier
        assertNotNull(createdHistorique.id)
        assertEquals(Statut.EMBARQUEMENT, createdHistorique.statut)
        assertEquals(createdVol.id, createdHistorique.idVol)
        assertNotNull(createdHistorique.datetime)

        verifyHistoriqueJson(
            client = webClient,
            id = createdHistorique.id!!,
            expectedStatut = "EMBARQUEMENT",
            expectedVolId = createdVol.id
        )

        val historiqueInDb = historiqueRepository.findById(createdHistorique.id!!).orElseThrow()
        assertEquals(Statut.EMBARQUEMENT, historiqueInDb.statut)
        assertEquals(createdVol.id, historiqueInDb.idVol)
    }

    @Test
    fun `should create and delete historique`() {
        val aeroportCentral = aeroportRepository.getCentralAirportId()
        val destination = webClient.createAeroport(aeroportRequest(codeIATA = "LYS"))

        val volRequest = volRequest(
            numeroVol = "AF-HIST-DEL",
            origineId = aeroportCentral,
            destinationId = destination.id
        )
        val createdVol = webClient.createVol(volRequest)

        val histRequest = historiqueRequest(
            idVol = createdVol.id!!,
            statut = Statut.DECOLLE
        )
        val createdHistorique = webClient.createHistorique(histRequest)

        assert(historiqueRepository.findById(createdHistorique.id!!).isPresent)

        webClient.deleteHistorique(createdHistorique.id)
        webClient.verifyHistoriqueDeleted(createdHistorique.id)

        assert(historiqueRepository.findById(createdHistorique.id).isEmpty)
    }

    @Test
    fun `should return 404 when historique not found`() {
        val nonExistentId = 999999L

        webClient.get()
            .uri("/api/internal-AWY/historiques/$nonExistentId")
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun `should return all historiques`() {
        val aeroportCentral = aeroportRepository.getCentralAirportId()
        val destination1 = webClient.createAeroport(aeroportRequest(nom = "Lyon", codeIATA = "LYS"))
        val destination2 = webClient.createAeroport(aeroportRequest(nom = "Marseille", codeIATA = "MRS"))

        val vol1 = webClient.createVol(volRequest("AF-H1", origineId = aeroportCentral, destinationId = destination1.id))
        val vol2 = webClient.createVol(volRequest("AF-H2", origineId = aeroportCentral, destinationId = destination2.id))

        webClient.createHistorique(historiqueRequest(vol1.id!!, Statut.EMBARQUEMENT))
        webClient.createHistorique(historiqueRequest(vol2.id!!, Statut.DECOLLE))

        val historiques = webClient.getAllHistoriques()
        assert(historiques.size >= 2) { "Expected at least 2 historiques, but got ${historiques.size}" }

        val historiquesInDb = historiqueRepository.findAll()
        assert(historiquesInDb.size >= 2)
    }

    @Test
    fun `should search historique by vol id`() {
        val aeroportCentral = aeroportRepository.getCentralAirportId()
        val destination = webClient.createAeroport(aeroportRequest(codeIATA = "LYS"))

        val volRequest = volRequest(
            numeroVol = "AF-SEARCH",
            origineId = aeroportCentral,
            destinationId = destination.id
        )
        val createdVol = webClient.createVol(volRequest)

        val histRequest = historiqueRequest(
            idVol = createdVol.id!!,
            statut = Statut.ENVOL
        )
        val createdHistorique = webClient.createHistorique(histRequest)

        val foundHistorique = webClient.searchHistoriqueByVolId(createdVol.id)

        assertEquals(createdHistorique.id, foundHistorique.id)
        assertEquals(Statut.ENVOL, foundHistorique.statut)
        assertEquals(createdVol.id, foundHistorique.idVol)
    }


    @Test
    fun `should create multiple historiques for same vol`() {
        val aeroportCentral = aeroportRepository.getCentralAirportId()
        val destination = webClient.createAeroport(aeroportRequest(codeIATA = "LYS"))

        val volRequest = volRequest(
            numeroVol = "AF-MULTI",
            origineId = aeroportCentral,
            destinationId = destination.id
        )
        val createdVol = webClient.createVol(volRequest)

        // Créer plusieurs historiques pour le même vol
        val hist1 = webClient.createHistorique(historiqueRequest(createdVol.id!!, Statut.ENATTENTE))
        val hist2 = webClient.createHistorique(historiqueRequest(createdVol.id, Statut.EMBARQUEMENT))
        val hist3 = webClient.createHistorique(historiqueRequest(createdVol.id, Statut.DECOLLE))

        assertNotNull(hist1.id)
        assertNotNull(hist2.id)
        assertNotNull(hist3.id)

        // Vérifier que tous existent en base
        val historiquesInDb = historiqueRepository.findAll()
            .filter { it.idVol == createdVol.id }
        assert(historiquesInDb.size >= 3) { "Expected at least 3 historiques for vol, but got ${historiquesInDb.size}" }
    }

    @Test
    fun `should create historique with different statuts`() {
        val aeroportCentral = aeroportRepository.getCentralAirportId()
        val destination1 = webClient.createAeroport(aeroportRequest(nom = "Lyon", codeIATA = "LYS"))
        val destination2 = webClient.createAeroport(aeroportRequest(nom = "Marseille", codeIATA = "MRS"))
        val destination3 = webClient.createAeroport(aeroportRequest(nom = "Nice", codeIATA = "NCE"))

        val vol1 = webClient.createVol(volRequest("AF-ST1", origineId = aeroportCentral, destinationId = destination1.id))
        val vol2 = webClient.createVol(volRequest("AF-ST2", origineId = aeroportCentral, destinationId = destination2.id))
        val vol3 = webClient.createVol(volRequest("AF-ST3", origineId = aeroportCentral, destinationId = destination3.id))

        val histEnAttente = webClient.createHistorique(historiqueRequest(vol1.id!!, Statut.ENATTENTE))
        val histEmbarquement = webClient.createHistorique(historiqueRequest(vol2.id!!, Statut.EMBARQUEMENT))
        val histDecolle = webClient.createHistorique(historiqueRequest(vol3.id!!, Statut.DECOLLE))

        assertEquals(Statut.ENATTENTE, historiqueRepository.findById(histEnAttente.id!!).orElseThrow().statut)
        assertEquals(Statut.EMBARQUEMENT, historiqueRepository.findById(histEmbarquement.id!!).orElseThrow().statut)
        assertEquals(Statut.DECOLLE, historiqueRepository.findById(histDecolle.id!!).orElseThrow().statut)
    }

    @Test
    fun `should delete historique by id`() {
        val aeroportCentral = aeroportRepository.getCentralAirportId()
        val destination = webClient.createAeroport(aeroportRequest(codeIATA = "LYS"))

        val volRequest = volRequest(
            numeroVol = "AF-DEL-TEST",
            origineId = aeroportCentral,
            destinationId = destination.id
        )
        val createdVol = webClient.createVol(volRequest)

        val histRequest = historiqueRequest(
            idVol = createdVol.id!!,
            statut = Statut.ARRIVE
        )
        val createdHistorique = webClient.createHistorique(histRequest)

        assert(historiqueRepository.findById(createdHistorique.id!!).isPresent)

        webClient.deleteHistorique(createdHistorique.id)

        webClient.get()
            .uri("/api/internal-AWY/historiques/${createdHistorique.id}")
            .exchange()
            .expectStatus().isNotFound

        assert(historiqueRepository.findById(createdHistorique.id).isEmpty)
    }
}
