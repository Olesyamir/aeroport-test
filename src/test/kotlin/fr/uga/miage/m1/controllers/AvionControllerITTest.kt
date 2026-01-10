package fr.uga.miage.m1.controllers

import fr.uga.miage.m1.Domain.enums.EtatMateriel
import fr.uga.miage.m1.Domain.updateRequest.UpdateAvionRequest
import fr.uga.miage.m1.Infrastucture.repository.AvionRepository
import fr.uga.miage.m1.Infrastucture.repository.HangarRepository
import fr.uga.miage.m1.Infrastucture.repository.PisteRepository
import fr.uga.miage.m1.Infrastucture.repository.VolRepository
import fr.uga.miage.m1.repository.AeroportRepository
import fr.uga.miage.m1.utils.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.reactive.server.WebTestClient
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AvionControllerITTest {

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
    fun `should create and retrieve avion`() {
        val request = avionRequest("F-AB123")
        val createdAvion = webClient.createAvion(request)

        verifyAvionJson(
            client = webClient,
            id = createdAvion.id!!,
            expectedNom = "Airbus A320",
            expectedImmatricule = "F-AB123",
            expectedType = "Passager",
            expectedCapacite = 180,
            expectedEtat = "AUSOL"
        )

        val avionInDb = avionRepository.findById(createdAvion.id).orElseThrow()
        assertEquals("Airbus A320", avionInDb.nom)
        assertEquals("F-AB123", avionInDb.numImmatricule)
        assertEquals("Passager", avionInDb.type)
        assertEquals(180, avionInDb.capacite)
        assertEquals(EtatMateriel.AUSOL, avionInDb.etat)
    }

    @Test
    fun `should create and delete avion`() {
        val request = avionRequest("F-XYZ123", "commercial")
        val createdAvion = webClient.createAvion(request)

        assert(avionRepository.findById(createdAvion.id!!).isPresent)

        webClient.deleteAvion(createdAvion.id)
        webClient.verifyAvionDeleted(createdAvion.id)

        assert(avionRepository.findById(createdAvion.id).isEmpty)
    }

    @Test
    fun `should update avion`() {

        val request = avionRequest("C-786")
        val createdAvion = webClient.createAvion(request)

        val updateRequest = UpdateAvionRequest(
            nom = "Airbus A321",
            numImmatricule = "F-AB123",
            type = "Long-courrier",
            capacite = 220,
            etat = EtatMateriel.MAINTENANCE,
        )
        webClient.updateAvion(createdAvion.id!!, updateRequest)

        verifyAvionJson(
            client = webClient,
            id = createdAvion.id!!,
            expectedNom = "Airbus A321",
            expectedImmatricule = "F-AB123",
            expectedType = "Long-courrier",
            expectedCapacite = 220,
            expectedEtat = "MAINTENANCE"
        )

        val avionInDb = avionRepository.findById(createdAvion.id).orElseThrow()
        assertEquals("Airbus A321", avionInDb.nom)
        assertEquals("F-AB123", avionInDb.numImmatricule)
        assertEquals("Long-courrier", avionInDb.type)
        assertEquals(220, avionInDb.capacite)
        assertEquals(EtatMateriel.MAINTENANCE, avionInDb.etat)

    }

    @Test
    fun `should return 404 when avion not found`() {
        val nonExistentId = 999999L

        webClient.get()
            .uri("/api/internal-AWY/avions/$nonExistentId")
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun `should return all avions`() {
        val request1 = avionRequest("F-CES01", "Cessna 172", "Leger", 40, EtatMateriel.AUSOL)
        val request2 = avionRequest("F-BOE01", "Boeing 747", "Cargo", 41, EtatMateriel.ENVOL)

        webClient.createAvion(request1)
        webClient.createAvion(request2)

        webClient.verifyEntityCountAtLeast("/api/internal-AWY/avions", 2)

        val avionsInDb = avionRepository.findAll()
        assert(avionsInDb.size >= 2)
    }

    @Test
    fun `should fail to create avion with duplicate numImmatricule`() {
        val request1 = avionRequest("F-CES01")
        val request2 = avionRequest("F-CES01")

        val createdAvion = webClient.createAvion(request1)

        webClient.verifyBadRequestMessage(
            uri = "/api/internal-AWY/avions",
            requestBody = request2,
            expectedMessagePart = "existe déjà"
        )

        val avionsInDb = avionRepository.findAll().filter { it.numImmatricule == "F-CES01" }
        assertEquals(1, avionsInDb.size)
        assertEquals(createdAvion.id, avionsInDb[0].id)
    }

    @Test
    fun `should delete avion by id`() {
        val request = avionRequest("F-DEL456", "Bombardier CRJ", "Regional", 90, EtatMateriel.AUSOL)
        val createdAvion = webClient.createAvion(request)

        assert(avionRepository.findById(createdAvion.id!!).isPresent)

        webClient.deleteAvion(createdAvion.id)

        webClient.get()
            .uri("/api/internal-AWY/avions/${createdAvion.id}")
            .exchange()
            .expectStatus().isNotFound

        assert(avionRepository.findById(createdAvion.id).isEmpty)
    }

    @Test
    fun `should find avion by numImmatricule`() {
        val request = avionRequest("F-DEL456", "Embraer E190", "Regional", 90, EtatMateriel.AUSOL)
        val createdAvion = webClient.createAvion(request)

        webClient.verifyAvionByImmatricule("F-DEL456", "Embraer E190")

        val avionInDb = avionRepository.findByNumImmatriculeOrIdNull("F-DEL456")
        assertNotNull(avionInDb)
        assertEquals("Embraer E190", avionInDb?.nom)
        assertEquals(createdAvion.id, avionInDb?.id)
    }


    @Test
    fun `should return 404 when searching non-existent numImmatricule`() {
        // Chercher un avion qui n'existe pas
        webClient.verifyAvionByImmatriculNotFound("F-XXXXX")

        val avionInDb = avionRepository.findByNumImmatriculeOrIdNull("F-XXXXX")
        org.junit.jupiter.api.Assertions.assertNull(avionInDb)
    }

    @Test
    fun `should search avion immediately after creation`() {
        val request = avionRequest(
            numImmatricule = "F-SEARCH",
            nom = "Boeing 737",
            type = "Commercial",
            capacite = 180,
            etat = EtatMateriel.AUSOL
        )
        val createdAvion = webClient.createAvion(request)

        val foundAvion = webClient.searchAvionByImmatricule("F-SEARCH")

        assertEquals(createdAvion.id, foundAvion.id)
        assertEquals("Boeing 737", foundAvion.nom)
        assertEquals("F-SEARCH", foundAvion.numImmatricule)

        val avionInDb = avionRepository.findByNumImmatriculeOrIdNull("F-SEARCH")
        assertNotNull(avionInDb)
        assertEquals(createdAvion.id, avionInDb?.id)
    }

}
