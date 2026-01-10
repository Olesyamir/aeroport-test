package fr.uga.miage.m1.controllers

import fr.uga.miage.m1.Application.aeroportCentrale.CentralAirport
import fr.uga.miage.m1.Domain.enums.Etat
import fr.uga.miage.m1.Domain.enums.Statut
import fr.uga.miage.m1.Domain.enums.TypeVol
import fr.uga.miage.m1.Domain.updateRequest.UpdateVolRequest
import fr.uga.miage.m1.Application.responseDTO.VolExterneBJAResponseDTO
import fr.uga.miage.m1.Infrastucture.repository.AvionRepository
import fr.uga.miage.m1.Infrastucture.repository.HangarRepository
import fr.uga.miage.m1.Infrastucture.repository.PisteRepository
import fr.uga.miage.m1.Infrastucture.repository.VolRepository
import fr.uga.miage.m1.repository.AeroportRepository
import fr.uga.miage.m1.models.AeroportEntity
import fr.uga.miage.m1.utils.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.client.RestTemplate
import java.time.LocalDateTime

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class VolControllerITTest {

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

    @MockitoBean
    lateinit var restTemplate: RestTemplate

    @Suppress("UNCHECKED_CAST")
    private fun <T> anyTypeRef(): ParameterizedTypeReference<T> =
        any(ParameterizedTypeReference::class.java) as ParameterizedTypeReference<T>?
            ?: object : ParameterizedTypeReference<T>() {}

    @BeforeEach
    fun cleanDatabase() {
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

        @Suppress("UNCHECKED_CAST")
        `when`(
            restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any<HttpEntity<*>>(),
                anyTypeRef<List<VolExterneBJAResponseDTO>>()
            )
        ).thenReturn(ResponseEntity.ok(emptyList()))
    }

    @Test
    fun `should create and retrieve vol`() {
        val aeroportCentral = aeroportRepository.getCentralAirportId()
        val destination = webClient.createAeroport(aeroportRequest(nom = "Lyon Saint-Exupéry", codeIATA = "LYS"))

        val request = volRequest(
            numeroVol = "AF123",
            compagnie = "Air France",
            origineId = aeroportCentral,
            destinationId = destination.id,
            statut = Statut.ENATTENTE,
            typeVol = TypeVol.NORMAL
        )

        val createdVol = webClient.createVol(request)

        verifyVolJson(
            client = webClient,
            id = createdVol.id!!,
            expectedNumero = "AF123",
            expectedCompagnie = "Air France",
            expectedOrigineId = aeroportCentral,
            expectedDestinationId = destination.id,
            expectedStatut = "ENATTENTE",
            expectedTypeVol = "NORMAL"
        )

        val volInDb = volRepository.findById(createdVol.id).orElseThrow()
        assertEquals("AF123", volInDb.numeroVol)
        assertEquals("Air France", volInDb.compagnie)
        assertEquals(Statut.ENATTENTE, volInDb.statut)
        assertEquals(TypeVol.NORMAL, volInDb.typeVol)
        assertEquals(aeroportCentral, volInDb.origine?.id)
    }

    @Test
    fun `should create vol with avion`() {
        val aeroportCentral = aeroportRepository.getCentralAirportId()
        val destination = webClient.createAeroport(aeroportRequest(codeIATA = "LYS"))
        val avion = webClient.createAvion(avionRequest("F-AVION1"))

        val request = volRequest(
            numeroVol = "AF456",
            origineId = aeroportCentral,
            destinationId = destination.id,
            avionId = avion.id
        )

        val createdVol = webClient.createVol(request)

        verifyVolJson(
            client = webClient,
            id = createdVol.id!!,
            expectedNumero = "AF456",
            expectedCompagnie = "Air France",
            expectedOrigineId = aeroportCentral,
            expectedDestinationId = destination.id,
            expectedStatut = "ENATTENTE",
            expectedTypeVol = "NORMAL",
            expectedAvionId = avion.id
        )

        val volInDb = volRepository.findById(createdVol.id).orElseThrow()
        assertNotNull(volInDb.avionEntity)
        assertEquals(avion.id, volInDb.avionEntity?.id)
    }

    @Test
    fun `should create vol with pistes`() {
        val aeroportCentral = aeroportRepository.getCentralAirportId()
        val destination = webClient.createAeroport(aeroportRequest(codeIATA = "LYS"))
        val pisteDecollage = webClient.createPiste(pisteRequest(longueur = 3500.0, etat = Etat.LIBRE))
        val pisteAtterissage = webClient.createPiste(pisteRequest(longueur = 3200.0, etat = Etat.LIBRE))

        val request = volRequest(
            numeroVol = "AF789",
            origineId = aeroportCentral,
            destinationId = destination.id,
            pisteDecollageId = pisteDecollage.id,
            pisteAtterissageId = pisteAtterissage.id
        )

        val createdVol = webClient.createVol(request)

        verifyVolJson(
            client = webClient,
            id = createdVol.id!!,
            expectedNumero = "AF789",
            expectedCompagnie = "Air France",
            expectedOrigineId = aeroportCentral,
            expectedDestinationId = destination.id,
            expectedStatut = "ENATTENTE",
            expectedTypeVol = "NORMAL",
            expectedPisteDecollageId = pisteDecollage.id,
            expectedPisteAtterissageId = pisteAtterissage.id
        )

        val volInDb = volRepository.findById(createdVol.id).orElseThrow()
        assertNotNull(volInDb.pisteDecollage)
        assertNotNull(volInDb.pisteAtterissage)
        assertEquals(pisteDecollage.id, volInDb.pisteDecollage?.id)
        assertEquals(pisteAtterissage.id, volInDb.pisteAtterissage?.id)
    }

    @Test
    fun `should create and delete vol`() {
        val aeroportCentral = aeroportRepository.getCentralAirportId()
        val destination = webClient.createAeroport(aeroportRequest(codeIATA = "LYS"))

        val request = volRequest(
            numeroVol = "AF-DEL1",
            origineId = aeroportCentral,
            destinationId = destination.id
        )

        val createdVol = webClient.createVol(request)

        assert(volRepository.findById(createdVol.id!!).isPresent)

        webClient.deleteVol(createdVol.id)
        webClient.verifyVolDeleted(createdVol.id)

        assert(volRepository.findById(createdVol.id).isEmpty)
    }

    @Test
    fun `should update vol`() {
        val aeroportCentral = aeroportRepository.getCentralAirportId()
        val destination = webClient.createAeroport(aeroportRequest(codeIATA = "LYS"))
        val nouvelleDestination = webClient.createAeroport(
            aeroportRequest(nom = "Marseille", codeIATA = "MRS")
        )

        val request = volRequest(
            numeroVol = "AF-UPD1",
            compagnie = "Air France",
            origineId = aeroportCentral,
            destinationId = destination.id,
            statut = Statut.ENATTENTE,
            typeVol = TypeVol.NORMAL
        )

        val createdVol = webClient.createVol(request)

        val now = LocalDateTime.now().plusDays(1).withSecond(0).withNano(0)

        val updateRequest = UpdateVolRequest(
            numeroVol = "AF-UPD2",
            compagnie = "Air France Updated",
            origineId = null,
            destinationId = nouvelleDestination.id,
            dateDepart = now,
            dateArrivee = now.plusHours(2),
            statut = Statut.ENATTENTE,
            typeVol = TypeVol.SURCLASSE,
            avionId = null,
            pisteDecollageId = null,
            pisteAtterissageId = null
        )

        webClient.updateVol(createdVol.id!!, updateRequest)

        verifyVolJson(
            client = webClient,
            id = createdVol.id!!,
            expectedNumero = "AF-UPD2",
            expectedCompagnie = "Air France Updated",
            expectedOrigineId = aeroportCentral,
            expectedDestinationId = nouvelleDestination.id,
            expectedStatut = "ENATTENTE",
            expectedTypeVol = "SURCLASSE"
        )

        val volInDb = volRepository.findById(createdVol.id!!).orElseThrow()
        assertEquals("AF-UPD2", volInDb.numeroVol)
        assertEquals("Air France Updated", volInDb.compagnie)
        assertEquals(Statut.ENATTENTE, volInDb.statut)
        assertEquals(TypeVol.SURCLASSE, volInDb.typeVol)
        assertEquals(aeroportCentral, volInDb.origine?.id)
        assertEquals(nouvelleDestination.id, volInDb.destination?.id)
    }

    @Test
    fun `should return 404 when vol not found`() {
        val nonExistentId = 999999L

        webClient.get()
            .uri("/api/vols/$nonExistentId")
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun `should return all vols`() {
        val aeroportCentral = aeroportRepository.getCentralAirportId()
        val destination1 = webClient.createAeroport(aeroportRequest(nom = "Lyon", codeIATA = "LYS"))
        val destination2 = webClient.createAeroport(aeroportRequest(nom = "Marseille", codeIATA = "MRS"))

        val request1 = volRequest("AF001", origineId = aeroportCentral, destinationId = destination1.id)
        val request2 = volRequest("AF002", origineId = aeroportCentral, destinationId = destination2.id)

        val created1 = webClient.createVol(request1)
        val created2 = webClient.createVol(request2)


        val allVols = webClient.getAllVols()

        // Il doit y avoir au moins 2 vols
        assert(allVols.size >= 2) {
            "Expected at least 2 vols, but got ${allVols.size}"
        }

        // Et nos 2 vols doivent être dans la liste
        val ids = allVols.mapNotNull { it.id }
        assert(ids.contains(created1.id)) { "AF001 not found in getAllVols" }
        assert(ids.contains(created2.id)) { "AF002 not found in getAllVols" }
    }

    @Test
    fun `should fail to create vol with duplicate numeroVol`() {
        val aeroportCentral = aeroportRepository.getCentralAirportId()
        val destination = webClient.createAeroport(aeroportRequest(codeIATA = "LYS"))

        val request1 = volRequest("AF-DUP", origineId = aeroportCentral, destinationId = destination.id)
        val request2 = volRequest("AF-DUP", origineId = aeroportCentral, destinationId = destination.id)

        val createdVol = webClient.createVol(request1)

        webClient.verifyBadRequestMessageForVol(
            requestBody = request2,
            expectedMessagePart = "existe déjà"
        )

        val volsInDb = volRepository.findAll().filter { it.numeroVol == "AF-DUP" }
        assertEquals(1, volsInDb.size)
        assertEquals(createdVol.id, volsInDb[0].id)
    }

    @Test
    fun `should find vol by numeroVol`() {
        val aeroportCentral = aeroportRepository.getCentralAirportId()
        val destination = webClient.createAeroport(aeroportRequest(codeIATA = "LYS"))

        val request = volRequest(
            numeroVol = "AF-FIND",
            compagnie = "Air France Find",
            origineId = aeroportCentral,
            destinationId = destination.id
        )

        val createdVol = webClient.createVol(request)

        val foundVol = webClient.searchVolByNumero("AF-FIND")

        assertEquals(createdVol.id, foundVol.id)
        assertEquals("AF-FIND", foundVol.numeroVol)
        assertEquals("Air France Find", foundVol.compagnie)

        val volInDb = volRepository.findById(createdVol.id!!).orElseThrow()
        assertEquals("AF-FIND", volInDb.numeroVol)
    }


    @Test
    fun `should list vols by statut`() {
        val aeroportCentral = aeroportRepository.getCentralAirportId()
        val destination1 = webClient.createAeroport(aeroportRequest(nom = "Lyon", codeIATA = "LYS"))
        val destination2 = webClient.createAeroport(aeroportRequest(nom = "Marseille", codeIATA = "MRS"))
        val destination3 = webClient.createAeroport(aeroportRequest(nom = "Nice", codeIATA = "NCE"))

        webClient.createVol(
            volRequest(
                "AF-ENATT1",
                origineId = aeroportCentral,
                destinationId = destination1.id,
                statut = Statut.ENATTENTE
            )
        )
        webClient.createVol(
            volRequest(
                "AF-ENATT2",
                origineId = aeroportCentral,
                destinationId = destination2.id,
                statut = Statut.ENATTENTE
            )
        )
        webClient.createVol(
            volRequest(
                "AF-ENVOL1",
                origineId = aeroportCentral,
                destinationId = destination3.id,
                statut = Statut.ENVOL
            )
        )

        val volsEnAttente = webClient.getVolsByStatut(Statut.ENATTENTE)
        assert(volsEnAttente.size >= 2) { "Expected at least 2 vols with statut ENATTENTE, but got ${volsEnAttente.size}" }

        val volsEnvol = webClient.getVolsByStatut(Statut.ENVOL)
        assert(volsEnvol.size >= 1) { "Expected at least 1 vol with statut ENVOL, but got ${volsEnvol.size}" }

        val volsInDb = volRepository.findAll().filter { it.statut == Statut.ENATTENTE }
        assert(volsInDb.size >= 2)
    }

    @Test
    fun `should get statut by vol id`() {
        val aeroportCentral = aeroportRepository.getCentralAirportId()
        val destination = webClient.createAeroport(aeroportRequest(codeIATA = "LYS"))

        val request = volRequest(
            numeroVol = "AF-STAT",
            origineId = aeroportCentral,
            destinationId = destination.id,
            statut = Statut.EMBARQUEMENT
        )

        val createdVol = webClient.createVol(request)

        val statut = webClient.getStatutById(createdVol.id!!)
        assertEquals(Statut.EMBARQUEMENT, statut)
    }

    @Test
    fun `should create vol with different typeVol`() {
        val aeroportCentral = aeroportRepository.getCentralAirportId()
        val destination1 = webClient.createAeroport(aeroportRequest(nom = "Lyon", codeIATA = "LYS"))
        val destination2 = webClient.createAeroport(aeroportRequest(nom = "Marseille", codeIATA = "MRS"))
        val destination3 = webClient.createAeroport(aeroportRequest(nom = "Nice", codeIATA = "NCE"))

        val volNormal = webClient.createVol(
            volRequest("AF-NORMAL", origineId = aeroportCentral, destinationId = destination1.id, typeVol = TypeVol.NORMAL)
        )
        val volSurclasse = webClient.createVol(
            volRequest("AF-SURCL", origineId = aeroportCentral, destinationId = destination2.id, typeVol = TypeVol.SURCLASSE)
        )
        val volUrgence = webClient.createVol(
            volRequest("AF-URG", origineId = aeroportCentral, destinationId = destination3.id, typeVol = TypeVol.URGENCE)
        )

        assertEquals(TypeVol.NORMAL, volRepository.findById(volNormal.id!!).orElseThrow().typeVol)
        assertEquals(TypeVol.SURCLASSE, volRepository.findById(volSurclasse.id!!).orElseThrow().typeVol)
        assertEquals(TypeVol.URGENCE, volRepository.findById(volUrgence.id!!).orElseThrow().typeVol)
    }


    @Test
    fun `should delete vol by id`() {
        val aeroportCentral = aeroportRepository.getCentralAirportId()
        val destination = webClient.createAeroport(aeroportRequest(codeIATA = "LYS"))

        val request = volRequest(
            numeroVol = "AF-DEL-TEST",
            origineId = aeroportCentral,
            destinationId = destination.id
        )

        val createdVol = webClient.createVol(request)

        assert(volRepository.findById(createdVol.id!!).isPresent)

        webClient.deleteVol(createdVol.id)

        webClient.get()
            .uri("/api/vols/${createdVol.id}")
            .exchange()
            .expectStatus().isNotFound

        assert(volRepository.findById(createdVol.id!!).isEmpty)
    }

    @Test
    fun `should create vol with all optional fields`() {
        val aeroportCentral = aeroportRepository.getCentralAirportId()
        val destination = webClient.createAeroport(aeroportRequest(codeIATA = "LYS"))
        val avion = webClient.createAvion(avionRequest("F-FULL"))
        val pisteDecollage = webClient.createPiste(pisteRequest(longueur = 3500.0))
        val pisteAtterissage = webClient.createPiste(pisteRequest(longueur = 3200.0))

        val request = volRequest(
            numeroVol = "AF-FULL",
            compagnie = "Air France Full",
            origineId = aeroportCentral,
            destinationId = destination.id,
            dateDepart = LocalDateTime.now(),
            dateArrivee = LocalDateTime.now().plusHours(2),
            statut = Statut.ENATTENTE,
            typeVol = TypeVol.NORMAL,
            avionId = avion.id,
            pisteDecollageId = pisteDecollage.id,
            pisteAtterissageId = pisteAtterissage.id
        )

        val createdVol = webClient.createVol(request)

        verifyVolJson(
            client = webClient,
            id = createdVol.id!!,
            expectedNumero = "AF-FULL",
            expectedCompagnie = "Air France Full",
            expectedOrigineId = aeroportCentral,
            expectedDestinationId = destination.id,
            expectedStatut = "ENATTENTE",
            expectedTypeVol = "NORMAL",
            expectedAvionId = avion.id,
            expectedPisteDecollageId = pisteDecollage.id,
            expectedPisteAtterissageId = pisteAtterissage.id
        )

        val volInDb = volRepository.findById(createdVol.id).orElseThrow()
        assertNotNull(volInDb.avionEntity)
        assertNotNull(volInDb.pisteDecollage)
        assertNotNull(volInDb.pisteAtterissage)
        assertNotNull(volInDb.origine)
        assertNotNull(volInDb.destination)
    }


}
