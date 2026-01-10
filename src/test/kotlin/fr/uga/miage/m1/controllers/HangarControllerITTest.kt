package fr.uga.miage.m1.controllers

import fr.uga.miage.m1.Domain.updateRequest.UpdateHangarRequest
import fr.uga.miage.m1.Domain.enums.Etat
import fr.uga.miage.m1.Infrastucture.repository.AvionRepository
import fr.uga.miage.m1.Infrastucture.repository.HangarRepository
import fr.uga.miage.m1.Infrastucture.repository.PisteRepository
import fr.uga.miage.m1.Infrastucture.repository.VolRepository
import fr.uga.miage.m1.utils.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.reactive.server.WebTestClient
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class HangarControllerITTest {

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

    @BeforeEach
    fun cleanDatabase() {
        volRepository.deleteAll()
        avionRepository.deleteAll()
        hangarRepository.deleteAll()
        pisteRepository.deleteAll()
    }

    @Test
    fun `should return 404 for unknown hangar`() {
        webClient.get()
            .uri("/notFound")
            .exchange()
            .expectStatus()
            .isNotFound
    }


    @Test
    fun `should create and retrieve hangar`() {

        val requestHangar = hangarRequest(
            capacite = 150,
            etat = Etat.LIBRE
        )
        val createdHangar = webClient.createHangar(requestHangar)

        webClient.verifyHangarJson(
            id = createdHangar.id!!,
            expectedCapacite = 150,
            expectedEtat = "LIBRE"
        )

        val hangarInDb = hangarRepository.findById(createdHangar.id).orElseThrow()
        assertEquals(150, hangarInDb.capacite)
        assertEquals(Etat.LIBRE, hangarInDb.etat)
    }

    @Test
    fun `should create and delete hangar`() {

        val request = hangarRequest()
        val createdHangar = webClient.createHangar(request)

        assert(hangarRepository.findById(createdHangar.id!!).isPresent)

        webClient.deleteHangar(createdHangar.id)
        webClient.verifyHangarDeleted(createdHangar.id)

        assert(hangarRepository.findById(createdHangar.id).isEmpty)
    }

    @Test
    fun `should update hangar`() {

        val createRequest = hangarRequest(
            capacite = 100,
            etat = Etat.LIBRE,
            avionIds = emptyList()
        )
        val createdHangar = webClient.createHangar(createRequest)

        val updateRequest = UpdateHangarRequest(
            capacite = 250,
            etat = Etat.OCCUPE,
            avionIds = mutableListOf()
        )
        webClient.updateHangar(createdHangar.id!!, updateRequest)

        webClient.verifyHangarJson(
            createdHangar.id!!,
            250,
            "OCCUPE"
        )

        val hangarInDb = hangarRepository.findById(createdHangar.id!!).orElseThrow()
        assertEquals(250, hangarInDb.capacite)
        assertEquals(Etat.OCCUPE, hangarInDb.etat)
    }

    @Test
    fun `should return 404 when hangar not found`() {
        val nonExistentId = 999999L

        webClient.get()
            .uri("/api/internal-AWY/hangars/$nonExistentId")
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun `should return all hangars`() {

        webClient.createHangar(
            hangarRequest(
                capacite = 100,
                etat = Etat.LIBRE
            )
        )
        webClient.createHangar(
            hangarRequest(
                capacite = 200,
                etat = Etat.OCCUPE,
            )
        )

        // get all hangars
        webClient.get()
            .uri("/api/internal-AWY/hangars")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.length()").value<Int> { count ->
                assert(count >= 2)
            }

        val hangarsInDb = hangarRepository.findAll()
        assert(hangarsInDb.size >= 2)
    }

}