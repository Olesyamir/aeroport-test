package fr.uga.miage.m1.controllers

import fr.uga.miage.m1.Application.aeroportCentrale.CentralAirport
import fr.uga.miage.m1.Domain.enums.Priorite
import fr.uga.miage.m1.Domain.enums.Usage
import fr.uga.miage.m1.Domain.updateRequest.UpdatePlanningPistesRequest
import fr.uga.miage.m1.Infrastucture.repository.*
import fr.uga.miage.m1.models.AeroportEntity
import fr.uga.miage.m1.repository.AeroportRepository
import fr.uga.miage.m1.utils.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.LocalTime

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PlanningPisteControllerITTest {

    @Autowired
    lateinit var webClient: WebTestClient

    @Autowired
    lateinit var planningPistesRepository: PlanningPistesRepository

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
        planningPistesRepository.deleteAll()
        volRepository.deleteAll()
        avionRepository.deleteAll()
        hangarRepository.deleteAll()
        pisteRepository.deleteAll()
        aeroportRepository.deleteAll()

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
    fun `should return 404 for unknown planning piste`() {
        webClient.get()
            .uri("/notFound")
            .exchange()
            .expectStatus()
            .isNotFound
    }

    @Test
    fun `should create and delete planning piste`() {
        val requestPlanningPiste = planningPisteRequest(
            startTime = LocalTime.of(8, 0),
            endTime = LocalTime.of(10, 0),
            priorite = Priorite.NORMALE,
            remarques = "Test planning",
            usage = Usage.DECOLLAGE
        )

        val createdPlanningPiste = webClient.createPlanningPiste(requestPlanningPiste)

        webClient.verifyPlanningPisteJson(
            id = createdPlanningPiste.id!!,
            expectedStartTime = LocalTime.of(8, 0),
            expectedEndTime = LocalTime.of(10, 0),
            expectedPriorite = Priorite.NORMALE,
            expectedUsage = Usage.DECOLLAGE
        )

        assert(planningPistesRepository.findById(createdPlanningPiste.id).isPresent)

        webClient.deletePlanningPiste(id = createdPlanningPiste.id)
        webClient.verifyPlanningPisteDeleted(id = createdPlanningPiste.id)

        assert(planningPistesRepository.findById(createdPlanningPiste.id).isEmpty)
    }

    @Test
    fun `should get planning piste by id via helper`() {
        val request = planningPisteRequest(
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(11, 0),
            priorite = Priorite.NORMALE,
            remarques = "Get by id helper",
            usage = Usage.DECOLLAGE
        )
        val createdPlanning = webClient.createPlanningPiste(request)

        val retrieved = webClient.getPlanningPiste(createdPlanning.id!!)

        assertEquals(createdPlanning.id, retrieved.id)
        assertEquals(LocalTime.of(9, 0), retrieved.startTime)
        assertEquals(LocalTime.of(11, 0), retrieved.endTime)
        assertEquals(Priorite.NORMALE, retrieved.priorite)
        assertEquals(Usage.DECOLLAGE, retrieved.usage)
    }


    @Test
    fun `should return 404 when planning piste not found`() {
        val nonExistentId = 999999L

        webClient.get()
            .uri("/api/internal-AWY/planningPistes/$nonExistentId")
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun `should return all planning pistes`() {
        webClient.createPlanningPiste(
            planningPisteRequest(
                startTime = LocalTime.of(8, 0),
                endTime = LocalTime.of(10, 0),
                priorite = Priorite.NORMALE,
                usage = Usage.DECOLLAGE
            )
        )
        webClient.createPlanningPiste(
            planningPisteRequest(
                startTime = LocalTime.of(11, 0),
                endTime = LocalTime.of(13, 0),
                priorite = Priorite.HAUTE,
                usage = Usage.ATTERISSAGE
            )
        )

        webClient.get()
            .uri("/api/internal-AWY/planningPistes")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.length()").value<Int> { count ->
                assert(count >= 2)
            }

        val planningsInDb = planningPistesRepository.findAll()
        assert(planningsInDb.size >= 2)
    }

    @Test
    fun `should return all planning pistes via helper`() {
        val planning1 = webClient.createPlanningPiste(
            planningPisteRequest(
                startTime = LocalTime.of(6, 0),
                endTime = LocalTime.of(7, 0),
                priorite = Priorite.NORMALE,
                usage = Usage.DECOLLAGE
            )
        )
        val planning2 = webClient.createPlanningPiste(
            planningPisteRequest(
                startTime = LocalTime.of(7, 0),
                endTime = LocalTime.of(8, 0),
                priorite = Priorite.HAUTE,
                usage = Usage.ATTERISSAGE
            )
        )

        val allPlannings = webClient.getAllPlanningPistes()

        assert(allPlannings.size >= 2)
        val ids = allPlannings.mapNotNull { it.id }
        assert(ids.contains(planning1.id))
        assert(ids.contains(planning2.id))
    }

    // TODO : resoudre creation vol

    @Test
    fun `should update planning piste`() {
        val createRequest = planningPisteRequest(
            startTime = LocalTime.of(8, 0),
            endTime = LocalTime.of(10, 0),
            priorite = Priorite.NORMALE,
            remarques = "Initial planning",
            usage = Usage.DECOLLAGE
        )
        val createdPlanning = webClient.createPlanningPiste(createRequest)

        val updateRequest = UpdatePlanningPistesRequest(
            startTime = LocalTime.of(14, 0),
            endTime = LocalTime.of(16, 0),
            priorite = Priorite.HAUTE,
            remarques = "Updated planning",
            usage = Usage.ATTERISSAGE,
            pisteId = null,
            volsId = emptyList()
        )
        webClient.updatePlanningPiste(createdPlanning.id!!, updateRequest)

        val updatedPlanning = planningPistesRepository.findById(createdPlanning.id!!).orElseThrow()
        assertEquals(LocalTime.of(14, 0), updatedPlanning.startTime)
        assertEquals(LocalTime.of(16, 0), updatedPlanning.endTime)
        assertEquals(Priorite.HAUTE, updatedPlanning.priorite)
        assertEquals(Usage.ATTERISSAGE, updatedPlanning.usage)
        assertEquals("Updated planning", updatedPlanning.remarques)
    }


    @Test
    fun `should create planning piste with different priorite levels`() {
        val planningNormale = webClient.createPlanningPiste(
            planningPisteRequest(
                startTime = LocalTime.of(8, 0),
                endTime = LocalTime.of(10, 0),
                priorite = Priorite.NORMALE,
                usage = Usage.DECOLLAGE
            )
        )

        val planningHaute = webClient.createPlanningPiste(
            planningPisteRequest(
                startTime = LocalTime.of(11, 0),
                endTime = LocalTime.of(13, 0),
                priorite = Priorite.HAUTE,
                usage = Usage.DECOLLAGE
            )
        )

        val planningUrgence = webClient.createPlanningPiste(
            planningPisteRequest(
                startTime = LocalTime.of(14, 0),
                endTime = LocalTime.of(16, 0),
                priorite = Priorite.URGENCE,
                usage = Usage.DECOLLAGE
            )
        )

        val normale = webClient.getPlanningPiste(planningNormale.id!!)
        val haute = webClient.getPlanningPiste(planningHaute.id!!)
        val urgence = webClient.getPlanningPiste(planningUrgence.id!!)

        assertEquals(Priorite.NORMALE, normale.priorite)
        assertEquals(Priorite.HAUTE, haute.priorite)
        assertEquals(Priorite.URGENCE, urgence.priorite)
    }

    @Test
    fun `should return 404 when updating non-existent planning piste`() {
        val updateRequest = UpdatePlanningPistesRequest(
            startTime = LocalTime.of(8, 0),
            endTime = LocalTime.of(10, 0),
            priorite = Priorite.NORMALE,
            remarques = null,
            usage = Usage.DECOLLAGE
        )

        webClient.put()
            .uri("/api/internal-AWY/planningPistes/999999")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(updateRequest)
            .exchange()
            .expectStatus().isNotFound
    }


    @Test
    fun `should return 404 when deleting non-existent planning piste`() {
        webClient.delete()
            .uri("/api/internal-AWY/planningPistes/999999")
            .exchange()
            .expectStatus().isNotFound
    }

}




