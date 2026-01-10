package fr.uga.miage.m1.controllers

import fr.uga.miage.m1.updateRequest.UpdateAeroportRequest
import fr.uga.miage.m1.Infrastucture.repository.AvionRepository
import fr.uga.miage.m1.Infrastucture.repository.HangarRepository
import fr.uga.miage.m1.Infrastucture.repository.PisteRepository
import fr.uga.miage.m1.Infrastucture.repository.VolRepository
import fr.uga.miage.m1.repository.AeroportRepository
import fr.uga.miage.m1.utils.*
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AeroportControllerITTest {

    @Autowired
    lateinit var webClient: WebTestClient

    @Autowired
    lateinit var avionRepository: AvionRepository

    @Autowired
    lateinit var hangarRepository: HangarRepository

    @Autowired
    lateinit var pisteRepository: PisteRepository

    @Autowired
    lateinit var volRepository: VolRepository

    @Autowired
    lateinit var aeroportRepository: AeroportRepository

    @BeforeEach
    fun cleanDatabase() {
        volRepository.deleteAll()
        avionRepository.deleteAll()
        hangarRepository.deleteAll()
        pisteRepository.deleteAll()
        aeroportRepository.deleteAll()
    }

    @Test
    fun `should return 404 for unknown aeroport`() {
        webClient.get()
            .uri("/notFound")
            .exchange()
            .expectStatus()
            .isNotFound
    }

    @Test
    fun `should create and retrieve aeroport`() {
        val request = aeroportRequest(
            nom = "Charles de Gaulle",
            ville = "Paris",
            pays = "France",
            codeIATA = "CDG"
        )
        val createdAeroport = webClient.createAeroport(request)

        webClient.verifyAeroportJson(
            id = createdAeroport.id,
            expectedNom = "Charles de Gaulle",
            expectedVille = "Paris",
            expectedPays = "France",
            expectedCodeIATA = "CDG"
        )

        val aeroportInDb = aeroportRepository.findById(createdAeroport.id).orElseThrow()
        assertEquals("Charles de Gaulle", aeroportInDb.nom)
        assertEquals("Paris", aeroportInDb.ville)
        assertEquals("France", aeroportInDb.pays)
        assertEquals("CDG", aeroportInDb.codeIATA)
    }

    @Test
    fun `should create and delete aeroport`() {
        val request = aeroportRequest(
            nom = "Orly",
            ville = "Paris",
            pays = "France",
            codeIATA = "ORY"
        )
        val createdAeroport = webClient.createAeroport(request)

        assert(aeroportRepository.findById(createdAeroport.id).isPresent)
        webClient.deleteAeroport(createdAeroport.id)
        webClient.verifyAeroportDeleted(createdAeroport.id)

        assert(aeroportRepository.findById(createdAeroport.id).isEmpty)
    }

    @Test
    fun `should return 404 when aeroport not found`() {
        val nonExistentId = 999999L

        webClient.get()
            .uri("/api/internal-AWY/aeroports/$nonExistentId")
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun `should return all aeroports`() {
        webClient.createAeroport(
            aeroportRequest(
                nom = "Charles de Gaulle",
                ville = "Paris",
                pays = "France",
                codeIATA = "CDG"
            )
        )
        webClient.createAeroport(
            aeroportRequest(
                nom = "Heathrow",
                ville = "London",
                pays = "UK",
                codeIATA = "LHR"
            )
        )

        // Obtenir tous les aéroports
        val allAeroports = webClient.getAllAeroports()
        assert(allAeroports.size >= 2)

        val aeroportsInDb = aeroportRepository.findAll()
        assert(aeroportsInDb.size >= 2)
    }

    @Test
    fun `should update aeroport`() {
        val createRequest = aeroportRequest(
            nom = "Charles de Gaulle",
            ville = "Paris",
            pays = "France",
            codeIATA = "CDG"
        )
        val createdAeroport = webClient.createAeroport(createRequest)

        val updateRequest = UpdateAeroportRequest(
            nom = "Aéroport Charles de Gaulle",
            ville = "Roissy",
            pays = "France",
            codeIATA = "CDG"
        )
        val updatedAeroport = webClient.updateAeroport(createdAeroport.id, updateRequest)

        assertEquals("Aéroport Charles de Gaulle", updatedAeroport.nom)
        assertEquals("Roissy", updatedAeroport.ville)

        val aeroportInDb = aeroportRepository.findById(createdAeroport.id).orElseThrow()
        assertEquals("Aéroport Charles de Gaulle", aeroportInDb.nom)
        assertEquals("Roissy", aeroportInDb.ville)
        assertEquals("France", aeroportInDb.pays)
        assertEquals("CDG", aeroportInDb.codeIATA)
    }

    @Test
    fun `should partially update aeroport`() {
        val createRequest = aeroportRequest(
            nom = "Charles de Gaulle",
            ville = "Paris",
            pays = "France",
            codeIATA = "CDG"
        )
        val createdAeroport = webClient.createAeroport(createRequest)

        val updateRequest = UpdateAeroportRequest(
            nom = "CDG Airport",
            ville = null,
            pays = null,
            codeIATA = null
        )
        val updatedAeroport = webClient.updateAeroport(createdAeroport.id, updateRequest)

        assertEquals("CDG Airport", updatedAeroport.nom)
        assertEquals("Paris", updatedAeroport.ville)
        assertEquals("France", updatedAeroport.pays)
        assertEquals("CDG", updatedAeroport.codeIATA)

        val aeroportInDb = aeroportRepository.findById(createdAeroport.id).orElseThrow()
        assertEquals("CDG Airport", aeroportInDb.nom)
        assertEquals("Paris", aeroportInDb.ville)
    }

    @Test
    fun `should search aeroport by code IATA`() {
        val request = aeroportRequest(
            nom = "Charles de Gaulle",
            ville = "Paris",
            pays = "France",
            codeIATA = "CDG"
        )
        val createdAeroport = webClient.createAeroport(request)

        val foundAeroport = webClient.searchAeroportByCodeIATA("CDG")

        assertNotNull(foundAeroport)
        assertEquals(createdAeroport.id, foundAeroport!!.id)
        assertEquals("Charles de Gaulle", foundAeroport.nom)
        assertEquals("CDG", foundAeroport.codeIATA)

        val aeroportInDb = aeroportRepository.findAll()
            .find { it.codeIATA == "CDG" }
        assertNotNull(aeroportInDb)
        assertEquals("Charles de Gaulle", aeroportInDb!!.nom)
    }

    @Test
    fun `should return null when searching non-existent code IATA`() {
        val foundAeroport = webClient.searchAeroportByCodeIATA("XXX")
        assertNull(foundAeroport)
    }

    @Test
    fun `should validate code IATA length is 3 characters`() {
        val invalidRequest = aeroportRequest(
            codeIATA = "ABCD"
        )

        webClient.verifyBadRequest(
            uri = "/api/internal-AWY/aeroports",
            requestBody = invalidRequest,
            expectedMessagePart = "Value too long for column \"codeiata",
            expectedStatus = HttpStatus.BAD_REQUEST
        )

        val aeroportsInDb = aeroportRepository.findAll()
            .filter { it.codeIATA == "ABCD" }
        assertEquals(0, aeroportsInDb.size)
    }

    @Transactional
    @Test
    fun `should return empty lists for aeroport with no vols`() {
        val aeroport = webClient.createAeroport(
            aeroportRequest(
                nom = "CD1",
                ville = "Paris",
                pays = "France",
                codeIATA = "CD1"
            )
        )

        val volsDepart = webClient.getVolsDepart(aeroport.id)
        val volsArrivee = webClient.getVolsArrivee(aeroport.id)

        assertEquals(0, volsDepart.size)
        assertEquals(0, volsArrivee.size)

        val aeroportInDb = aeroportRepository.findById(aeroport.id).orElseThrow()
        assertEquals(0, aeroportInDb.volsDepart.size)
        assertEquals(0, aeroportInDb.volsArrivee.size)
    }

    @Test
    fun `should delete aeroport by code IATA`() {
        val request = aeroportRequest(
            nom = "Charles de Gaulle",
            ville = "Paris",
            pays = "France",
            codeIATA = "CDG"
        )
        val createdAeroport = webClient.createAeroport(request)

        webClient.verifyAeroportJson(
            id = createdAeroport.id,
            expectedNom = "Charles de Gaulle",
            expectedVille = "Paris",
            expectedPays = "France",
            expectedCodeIATA = "CDG"
        )

        webClient.deleteAeroport(createdAeroport.id)
        webClient.verifyAeroportDeleted(createdAeroport.id)
        assert(aeroportRepository.findById(createdAeroport.id).isEmpty)

    }


}

// TODO : getVolsDepart, getVolsArrivee
