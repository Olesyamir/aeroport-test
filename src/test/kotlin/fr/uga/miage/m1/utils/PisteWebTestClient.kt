package fr.uga.miage.m1.utils

import fr.uga.miage.m1.Application.responseDTO.PisteResponseDTO
import fr.uga.miage.m1.Domain.updateRequest.UpdatePisteRequest
import fr.uga.miage.m1.Domain.enums.Etat
import fr.uga.miage.m1.Application.requestDTO.PisteRequestDTO
import org.hamcrest.Matchers
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient

fun WebTestClient.createPiste(request: PisteRequestDTO): PisteResponseDTO {
    return this.post()
        .uri("/api/internal-AWY/pistes")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(request)
        .exchange()
        .expectStatus().isOk
        .expectBody(PisteResponseDTO::class.java)
        .returnResult()
        .responseBody!!
}

fun WebTestClient.deletePiste(id: Long) {
    this.delete()
        .uri("/api/internal-AWY/pistes/$id")
        .exchange()
        .expectStatus().isOk
}

fun WebTestClient.verifyPisteDeleted(id: Long) {
    this.get()
        .uri("/api/internal-AWY/pistes/$id")
        .exchange()
        .expectStatus().isNotFound
}

fun WebTestClient.updatePiste(id: Long, update: UpdatePisteRequest) {
    this.put()
        .uri("/api/internal-AWY/pistes/$id")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(update)
        .exchange()
        .expectStatus().isOk
}

fun WebTestClient.verifyPisteJson(
    id: Long,
    expectedLongueur: Double,
    expectedEtat: Etat,
    expectedAvionsCount: Int? = null,
    expectedAvionIds: List<Long>? = null
) {
    val spec = this.get()
        .uri("/api/internal-AWY/pistes/$id")
        .exchange()
        .expectStatus().isOk
        .expectBody()
        .jsonPath("$.longueur").isEqualTo(expectedLongueur)
        .jsonPath("$.etat").isEqualTo(expectedEtat.toString())

    if (expectedAvionsCount != null) {
        spec.jsonPath("$.avions.length()").isEqualTo(expectedAvionsCount)
    }

    if (!expectedAvionIds.isNullOrEmpty()) {
        expectedAvionIds.forEachIndexed { index, expectedId ->
            spec.jsonPath("$.avions[$index].id").isEqualTo(expectedId)
        }
    }
}


fun WebTestClient.getPiste(id: Long): PisteResponseDTO {
    return this.get()
        .uri("/api/internal-AWY/pistes/$id")
        .exchange()
        .expectStatus().isOk
        .expectBody(PisteResponseDTO::class.java)
        .returnResult()
        .responseBody!!
}


fun WebTestClient.getAllPistes(): List<PisteResponseDTO> {
    return this.get()
        .uri("/api/internal-AWY/pistes")
        .exchange()
        .expectStatus().isOk
        .expectBodyList(PisteResponseDTO::class.java)
        .returnResult()
        .responseBody!!
}

fun WebTestClient.verifyBadRequestMessageForPiste(
    uri: String,
    requestBody: Any,
    expectedMessagePart: String
) {
    this.post()
        .uri(uri)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(requestBody)
        .exchange()
        .expectStatus().isBadRequest
        .expectBody()
        .jsonPath("$.message").value(Matchers.containsString(expectedMessagePart))
}