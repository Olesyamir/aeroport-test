package fr.uga.miage.m1.utils

import fr.uga.miage.m1.Application.responseDTO.HistoriqueResponseDTO
import fr.uga.miage.m1.Application.responseDTO.PlanningPistesResponseDTO
import fr.uga.miage.m1.Domain.enums.Statut
import fr.uga.miage.m1.reponses.TableauDeBordTrafficDTO
import fr.uga.miage.m1.reponses.VolTrafficDTO
import org.springframework.test.web.reactive.server.WebTestClient


fun WebTestClient.getTableauDeBord(aeroportId: Long): TableauDeBordTrafficDTO {
    return this.get()
        .uri("/api/tableau-bord/$aeroportId")
        .exchange()
        .expectStatus().isOk
        .expectBody(TableauDeBordTrafficDTO::class.java)
        .returnResult()
        .responseBody!!
}

fun WebTestClient.getVolsDepartTraffic(aeroportId: Long): List<VolTrafficDTO> {
    return this.get()
        .uri("/api/tableau-bord/$aeroportId/vols-depart")
        .exchange()
        .expectStatus().isOk
        .expectBodyList(VolTrafficDTO::class.java)
        .returnResult()
        .responseBody!!
}

fun WebTestClient.getVolsArriveeTraffic(aeroportId: Long): List<VolTrafficDTO> {
    return this.get()
        .uri("/api/tableau-bord/$aeroportId/vols-arrivee")
        .exchange()
        .expectStatus().isOk
        .expectBodyList(VolTrafficDTO::class.java)
        .returnResult()
        .responseBody!!
}

fun WebTestClient.getVolsParStatutTraffic(aeroportId: Long, statut: Statut): List<VolTrafficDTO> {
    return this.get()
        .uri("/api/tableau-bord/$aeroportId/vols-par-statut/$statut")
        .exchange()
        .expectStatus().isOk
        .expectBodyList(VolTrafficDTO::class.java)
        .returnResult()
        .responseBody!!
}

fun WebTestClient.getVolsEnCoursTraffic(aeroportId: Long): List<VolTrafficDTO> {
    return this.get()
        .uri("/api/tableau-bord/$aeroportId/vols-en-cours")
        .exchange()
        .expectStatus().isOk
        .expectBodyList(VolTrafficDTO::class.java)
        .returnResult()
        .responseBody!!
}

fun WebTestClient.getHistoriqueVolTraffic(volId: Long): HistoriqueResponseDTO? {
    return try {
        this.get()
            .uri("/api/tableau-bord/vol/$volId/historique")
            .exchange()
            .expectStatus().isOk
            .expectBody(HistoriqueResponseDTO::class.java)
            .returnResult()
            .responseBody
    } catch (e: Exception) {
        null
    }
}

fun WebTestClient.getHistoriquesAeroportTraffic(aeroportId: Long): List<HistoriqueResponseDTO> {
    return this.get()
        .uri("/api/tableau-bord/$aeroportId/historiques")
        .exchange()
        .expectStatus().isOk
        .expectBodyList(HistoriqueResponseDTO::class.java)
        .returnResult()
        .responseBody!!
}

fun WebTestClient.getPlanningsPistesAeroportTraffic(aeroportId: Long): List<PlanningPistesResponseDTO> {
    return this.get()
        .uri("/api/tableau-bord/$aeroportId/plannings-pistes")
        .exchange()
        .expectStatus().isOk
        .expectBodyList(PlanningPistesResponseDTO::class.java)
        .returnResult()
        .responseBody!!
}

fun WebTestClient.getPlanningPisteTraffic(pisteId: Long): List<PlanningPistesResponseDTO> {
    return this.get()
        .uri("/api/tableau-bord/piste/$pisteId/planning")
        .exchange()
        .expectStatus().isOk
        .expectBodyList(PlanningPistesResponseDTO::class.java)
        .returnResult()
        .responseBody!!
}

fun WebTestClient.getVolsDepartBja(): List<VolTrafficDTO> {
    return this.get()
        .uri("/api/tableau-bord/partenaires/BJA/departs")
        .exchange()
        .expectStatus().isOk
        .expectBodyList(VolTrafficDTO::class.java)
        .returnResult()
        .responseBody!!
}

fun WebTestClient.getVolsArriveeBja(): List<VolTrafficDTO> {
    return this.get()
        .uri("/api/tableau-bord/partenaires/BJA/arrivees")
        .exchange()
        .expectStatus().isOk
        .expectBodyList(VolTrafficDTO::class.java)
        .returnResult()
        .responseBody!!
}

fun WebTestClient.verifyTableauDeBordNotFound(aeroportId: Long) {
    this.get()
        .uri("/api/tableau-bord/$aeroportId")
        .exchange()
        .expectStatus().isNotFound
}