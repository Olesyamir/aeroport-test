package fr.uga.miage.m1.controllers

import fr.uga.miage.m1.Domain.enums.Etat
import fr.uga.miage.m1.Domain.enums.EtatMateriel
import fr.uga.miage.m1.Domain.enums.Statut
import fr.uga.miage.m1.Domain.enums.TypeVol
import fr.uga.miage.m1.Domain.updateRequest.UpdatePisteRequest
import fr.uga.miage.m1.Infrastucture.repository.AvionRepository
import fr.uga.miage.m1.Infrastucture.repository.HangarRepository
import fr.uga.miage.m1.Infrastucture.repository.PisteRepository
import fr.uga.miage.m1.Infrastucture.repository.VolRepository
import fr.uga.miage.m1.repository.AeroportRepository
import fr.uga.miage.m1.utils.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.LocalDateTime


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PisteControllerITTest {

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
    fun `should return 404 for unknown piste`() {
        webClient.get()
            .uri("/notFound")
            .exchange()
            .expectStatus()
            .isNotFound
    }


    @Test
    fun `should create and delete piste`() {


        val requestPiste = pisteRequest(
            longueur = 3000.0,
            etat = Etat.LIBRE,
        )
        val createdPiste = webClient.createPiste(requestPiste)

        webClient.verifyPisteJson(
            id = createdPiste.id!!,
            expectedLongueur = 3000.0,
            expectedEtat = Etat.LIBRE
        )

        assert(pisteRepository.findById(createdPiste.id!!).isPresent)

        webClient.deletePiste(id = createdPiste.id!!)
        webClient.verifyPisteDeleted(id = createdPiste.id!!)

        assert(pisteRepository.findById(createdPiste.id!!).isEmpty)
    }

    @Test
    fun `should return all pistes`() {
        webClient.createPiste(
            pisteRequest(
                longueur = 3000.0,
                etat = Etat.LIBRE,
            )
        )
        webClient.createPiste(
            pisteRequest(
                longueur = 4000.0,
                etat = Etat.OCCUPE,
            )
        )

        webClient.get()
            .uri("/api/internal-AWY/pistes")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.length()").value<Int> { count ->
                assert(count >= 2)
            }

        val pistesInDb = pisteRepository.findAll()
        assert(pistesInDb.size >= 2)
    }


    @Test
    fun `should update piste longueur and etat`() {
        val aeroport = webClient.createAeroport(
            aeroportRequest(
                nom = "Test Airport",
                codeIATA = "UP1",
                ville = "Test City",
                pays = "Test Country"
            )
        )

        val pisteRequest = pisteRequest(
            longueur = 3000.0,
            etat = Etat.LIBRE,
        )
        val createdPiste = webClient.createPiste(pisteRequest)

        val updateRequest = UpdatePisteRequest(
            longueur = 3500.0,
            etat = Etat.OCCUPE,
            volsDepartIds = emptyList(),
            volsArriveeIds = emptyList()
        )
        webClient.updatePiste(createdPiste.id!!, updateRequest)

        val updatedPiste = webClient.getPiste(createdPiste.id!!)
        assertEquals(3500.0, updatedPiste.longueur)
        assertEquals(Etat.OCCUPE, updatedPiste.etat)

        val pisteInDb = pisteRepository.findById(createdPiste.id!!).orElseThrow()
        assertEquals(3500.0, pisteInDb.longueur)
        assertEquals(Etat.OCCUPE, pisteInDb.etat)
    }

//    @Test
//    fun `should update piste with vols depart`() {
//        // 1. Créer aéroport
//        val aeroport = webClient.createAeroport(
//            aeroportRequest(
//                nom = "Test Airport",
//                codeIATA = "UP2",
//                ville = "Test City",
//                pays = "Test Country"
//            )
//        )
//
//        // 2. Créer piste
//        val piste = webClient.createPiste(
//            pisteRequest(
//                longueur = 3000.0,
//                etat = Etat.LIBRE,
//                aeroportId = aeroport.id
//            )
//        )
//
//        // 3. Créer avion
//        val avion = webClient.createAvion(
//            avionRequest("F-UP2", "Boeing 737", "Commercial", 180, EtatMateriel.AUSOL)
//        )
//
//        // 4. Créer vol
//        val vol = webClient.createVol(
//            volRequest(
//                numeroVol = "AF001",
//                compagnie = "Air France",
//                dateDepart = LocalDateTime.now().plusDays(1),
//                dateArrivee = LocalDateTime.now().plusDays(1).plusHours(2),
//                statut = Statut.PREVU,
//                typeVol = TypeVol.SURCLASSE,
//                avionId = avion.id!!,
//                origineId = aeroport.id,
//                destinationId = aeroport.id
//            )
//        )
//
//        // 5. Mettre à jour piste avec vol de départ
//        val updateRequest = UpdatePisteRequest(
//            longueur = 3000.0,
//            etat = Etat.OCCUPE,
//            volsDepartIds = listOf(vol.id!!),
//            volsArriveeIds = emptyList()
//        )
//        webClient.updatePiste(piste.id!!, updateRequest)
//
//        // 6. Vérifier via API
//        val updatedPiste = webClient.getPiste(piste.id!!)
//        assertEquals(1, updatedPiste.volsDepart?.size)
//        assertTrue(updatedPiste.volsDepart?.contains(vol.id) == true)
//
//        // ✅ Vérifier en BDD
//        val pisteInDb = pisteRepository.findById(piste.id!!).orElseThrow()
//        assertEquals(1, pisteInDb.volsDepart.size)
//        assertEquals(vol.id, pisteInDb.volsDepart.first().id)
//
//        // ✅ Vérifier que le vol est lié à la piste
//        val volInDb = volRepository.findById(vol.id!!).orElseThrow()
//        assertEquals(piste.id, volInDb.pisteDecollage?.id)
//    }
//
//    @Test
//    fun `should update piste with vols arrivee`() {
//        // 1. Créer aéroport
//        val aeroport = webClient.createAeroport(
//            aeroportRequest(
//                nom = "Test Airport",
//                codeIATA = "UP3",
//                ville = "Test City",
//                pays = "Test Country"
//            )
//        )
//
//        // 2. Créer piste
//        val piste = webClient.createPiste(
//            pisteRequest(
//                longueur = 3000.0,
//                etat = Etat.LIBRE,
//                aeroportId = aeroport.id
//            )
//        )
//
//        // 3. Créer avion
//        val avion = webClient.createAvion(
//            avionRequest("F-UP3", "Airbus A320", "Commercial", 150, EtatMateriel.ENVOL)
//        )
//
//        // 4. Créer vol
//        val vol = webClient.createVol(
//            volRequest(
//                numeroVol = "BA002",
//                compagnie = "British Airways",
//                dateDepart = LocalDateTime.now().plusDays(1),
//                dateArrivee = LocalDateTime.now().plusDays(1).plusHours(2),
//                statut = Statut.ENATTENTE,
//                typeVol = TypeVol.TEST,
//                avionId = avion.id!!,
//                origineId = aeroport.id,
//                destinationId = aeroport.id
//            )
//        )
//
//        // 5. Mettre à jour piste avec vol d'arrivée
//        val updateRequest = UpdatePisteRequest(
//            longueur = 3000.0,
//            etat = Etat.OCCUPE,
//            volsDepartIds = emptyList(),
//            volsArriveeIds = listOf(vol.id!!)
//        )
//        webClient.updatePiste(piste.id!!, updateRequest)
//
//        val updatedPiste = webClient.getPiste(piste.id!!)
//        assertEquals(1, updatedPiste.volsArrivee?.size)
//        assertTrue(updatedPiste.volsArrivee?.contains(vol.id) == true)
//
//        // Vérifier en BDD
//        val pisteInDb = pisteRepository.findById(piste.id!!).orElseThrow()
//        assertEquals(1, pisteInDb.volsArrivee.size)
//        assertEquals(vol.id, pisteInDb.volsArrivee.first().id)
//
//        // Vérifier que le vol est lié à la piste
//        val volInDb = volRepository.findById(vol.id!!).orElseThrow()
//        assertEquals(piste.id, volInDb.pisteAtterissage?.id)
//    }
//
//    @Test
//    fun `should update piste with both vols depart and arrivee`() {
//        // 1. Créer aéroport
//        val aeroport = webClient.createAeroport(
//            aeroportRequest(
//                nom = "Test Airport",
//                codeIATA = "UP4",
//                ville = "Test City",
//                pays = "Test Country"
//            )
//        )
//
//        // 2. Créer piste
//        val piste = webClient.createPiste(
//            pisteRequest(
//                longueur = 3000.0,
//                etat = Etat.LIBRE,
//                aeroportId = aeroport.id
//            )
//        )
//
//        // 3. Créer avions
//        val avion1 = webClient.createAvion(
//            avionRequest("F-UP4A", "Boeing 737", "Commercial", 180, EtatMateriel.AUSOL)
//        )
//        val avion2 = webClient.createAvion(
//            avionRequest("F-UP4B", "Airbus A320", "Commercial", 150, EtatMateriel.ENVOL)
//        )
//
//        // 4. Créer vols
//        val volDepart = webClient.createVol(
//            volRequest(
//                numeroVol = "AF001",
//                avionId = avion1.id!!,
//                origineId = aeroport.id,
//                destinationId = aeroport.id
//            )
//        )
//        val volArrivee = webClient.createVol(
//            volRequest(
//                numeroVol = "BA002",
//                avionId = avion2.id!!,
//                origineId = aeroport.id,
//                destinationId = aeroport.id
//            )
//        )
//
//        // 5. Mettre à jour piste avec les deux vols
//        val updateRequest = UpdatePisteRequest(
//            longueur = 3000.0,
//            etat = Etat.OCCUPE,
//            volsDepartIds = listOf(volDepart.id!!),
//            volsArriveeIds = listOf(volArrivee.id!!)
//        )
//        webClient.updatePiste(piste.id!!, updateRequest)
//
//        // 6. Vérifier via API
//        val updatedPiste = webClient.getPiste(piste.id!!)
//        assertEquals(1, updatedPiste.volsDepart?.size)
//        assertEquals(1, updatedPiste.volsArrivee?.size)
//        assertTrue(updatedPiste.volsDepart?.contains(volDepart.id) == true)
//        assertTrue(updatedPiste.volsArrivee?.contains(volArrivee.id) == true)
//
//        // ✅ Vérifier en BDD
//        val pisteInDb = pisteRepository.findById(piste.id!!).orElseThrow()
//        assertEquals(1, pisteInDb.volsDepart.size)
//        assertEquals(1, pisteInDb.volsArrivee.size)
//    }
//
//    @Test
//    fun `should replace vols when updating piste`() {
//        // 1. Créer aéroport
//        val aeroport = webClient.createAeroport(
//            aeroportRequest(
//                nom = "Test Airport",
//                codeIATA = "UP5",
//                ville = "Test City",
//                pays = "Test Country"
//            )
//        )
//
//        // 2. Créer piste
//        val piste = webClient.createPiste(
//            pisteRequest(
//                longueur = 3000.0,
//                etat = Etat.LIBRE,
//                aeroportId = aeroport.id
//            )
//        )
//
//        // 3. Créer avions
//        val avion1 = webClient.createAvion(
//            avionRequest("F-UP5A", "Boeing 737", "Commercial", 180, EtatMateriel.AUSOL)
//        )
//        val avion2 = webClient.createAvion(
//            avionRequest("F-UP5B", "Airbus A320", "Commercial", 150, EtatMateriel.AUSOL)
//        )
//
//        // 4. Créer vols
//        val vol1 = webClient.createVol(
//            volRequest(
//                numeroVol = "AF001",
//                avionId = avion1.id!!,
//                origineId = aeroport.id,
//                destinationId = aeroport.id
//            )
//        )
//        val vol2 = webClient.createVol(
//            volRequest(
//                numeroVol = "AF002",
//                avionId = avion2.id!!,
//                origineId = aeroport.id,
//                destinationId = aeroport.id
//            )
//        )
//
//        // 5. Premier update avec vol1
//        webClient.updatePiste(
//            piste.id!!,
//            UpdatePisteRequest(
//                longueur = 3000.0,
//                etat = Etat.OCCUPE,
//                volsDepartIds = listOf(vol1.id!!),
//                volsArriveeIds = emptyList()
//            )
//        )
//
//        // 6. Deuxième update avec vol2 (remplace vol1)
//        webClient.updatePiste(
//            piste.id!!,
//            UpdatePisteRequest(
//                longueur = 3000.0,
//                etat = Etat.OCCUPE,
//                volsDepartIds = listOf(vol2.id!!),
//                volsArriveeIds = emptyList()
//            )
//        )
//
//        // 7. Vérifier via API
//        val updatedPiste = webClient.getPiste(piste.id!!)
//        assertEquals(1, updatedPiste.volsDepart?.size)
//        assertTrue(updatedPiste.volsDepart?.contains(vol2.id) == true)
//        assertFalse(updatedPiste.volsDepart?.contains(vol1.id) == true)
//
//        // ✅ Vérifier en BDD
//        val pisteInDb = pisteRepository.findById(piste.id!!).orElseThrow()
//        assertEquals(1, pisteInDb.volsDepart.size)
//        assertEquals(vol2.id, pisteInDb.volsDepart.first().id)
//
//        // ✅ Vérifier que vol1 n'est plus lié à la piste
//        val vol1InDb = volRepository.findById(vol1.id!!).orElseThrow()
//        assertNull(vol1InDb.pisteDecollage)
//
//        // ✅ Vérifier que vol2 est lié à la piste
//        val vol2InDb = volRepository.findById(vol2.id!!).orElseThrow()
//        assertEquals(piste.id, vol2InDb.pisteDecollage?.id)
//    }
//
//    @Test
//    fun `should remove all vols when updating with empty lists`() {
//        // 1. Créer aéroport
//        val aeroport = webClient.createAeroport(
//            aeroportRequest(
//                nom = "Test Airport",
//                codeIATA = "UP6",
//                ville = "Test City",
//                pays = "Test Country"
//            )
//        )
//
//        // 2. Créer piste
//        val piste = webClient.createPiste(
//            pisteRequest(
//                longueur = 3000.0,
//                etat = Etat.LIBRE,
//                aeroportId = aeroport.id
//            )
//        )
//
//        // 3. Créer avion et vol
//        val avion = webClient.createAvion(
//            avionRequest("F-UP6", "Boeing 737", "Commercial", 180, EtatMateriel.AUSOL)
//        )
//        val vol = webClient.createVol(
//            volRequest(
//                numeroVol = "AF001",
//                avionId = avion.id!!,
//                origineId = aeroport.id,
//                destinationId = aeroport.id
//            )
//        )
//
//        // 4. Ajouter vol à la piste
//        webClient.updatePiste(
//            piste.id!!,
//            UpdatePisteRequest(
//                longueur = 3000.0,
//                etat = Etat.OCCUPE,
//                volsDepartIds = listOf(vol.id!!),
//                volsArriveeIds = emptyList()
//            )
//        )
//
//        // 5. Supprimer le vol (liste vide)
//        webClient.updatePiste(
//            piste.id!!,
//            UpdatePisteRequest(
//                longueur = 3000.0,
//                etat = Etat.LIBRE,
//                volsDepartIds = emptyList(),
//                volsArriveeIds = emptyList()
//            )
//        )
//
//        // 6. Vérifier via API
//        val updatedPiste = webClient.getPiste(piste.id!!)
//        assertTrue(updatedPiste.volsDepart?.isEmpty() == true)
//        assertTrue(updatedPiste.volsArrivee?.isEmpty() == true)
//
//        // ✅ Vérifier en BDD
//        val pisteInDb = pisteRepository.findById(piste.id!!).orElseThrow()
//        assertEquals(0, pisteInDb.volsDepart.size)
//        assertEquals(0, pisteInDb.volsArrivee.size)
//
//        // ✅ Vérifier que le vol n'est plus lié
//        val volInDb = volRepository.findById(vol.id!!).orElseThrow()
//        assertNull(volInDb.pisteDecollage)
//    }

    @Test
    fun `should return 404 when updating non-existent piste`() {
        val updateRequest = UpdatePisteRequest(
            longueur = 3000.0,
            etat = Etat.OCCUPE,
            volsDepartIds = emptyList(),
            volsArriveeIds = emptyList()
        )

        webClient.put()
            .uri("/api/internal-AWY/pistes/999999")
            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .bodyValue(updateRequest)
            .exchange()
            .expectStatus().isNotFound
    }
}