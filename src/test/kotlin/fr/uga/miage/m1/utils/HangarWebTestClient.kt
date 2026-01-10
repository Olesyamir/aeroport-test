package fr.uga.miage.m1.utils

import fr.uga.miage.m1.Application.responseDTO.HangarResponseDTO
import fr.uga.miage.m1.Application.requestDTO.HangarRequestDTO
import fr.uga.miage.m1.Domain.updateRequest.UpdateHangarRequest
import org.hamcrest.Matchers
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient


fun WebTestClient.createHangar(request: HangarRequestDTO): HangarResponseDTO {
    return this.post()
        .uri("/api/internal-AWY/hangars")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(request)
        .exchange()
        .expectStatus().isOk
        .expectBody(HangarResponseDTO::class.java)
        .returnResult()
        .responseBody!!
}

fun WebTestClient.deleteHangar(id: Long) {
    this.delete()
        .uri("/api/internal-AWY/hangars/$id")
        .exchange()
        .expectStatus().isOk
}

fun WebTestClient.verifyHangarDeleted(id: Long) {
    this.get()
        .uri("/api/internal-AWY/hangars/$id")
        .exchange()
        .expectStatus().isNotFound
}

fun WebTestClient.updateHangar(id: Long, update: UpdateHangarRequest) {
    this.put()
        .uri("/api/internal-AWY/hangars/$id")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(update)
        .exchange()
        .expectStatus().isOk
}

fun WebTestClient.verifyHangarJson(
    id: Long,
    expectedCapacite: Int,
    expectedEtat: String,
    expectedAvionsCount: Int? = null,
    expectedAvionIds: List<Long>? = null
) {
    val spec = this.get()
        .uri("/api/internal-AWY/hangars/$id")
        .exchange()
        .expectStatus().isOk
        .expectBody()
        .jsonPath("$.capacite").isEqualTo(expectedCapacite)
        .jsonPath("$.etat").isEqualTo(expectedEtat)

    if (expectedAvionsCount != null) {
        spec.jsonPath("$.avions.length()").isEqualTo(expectedAvionsCount)
    }

    if (expectedAvionIds != null && expectedAvionIds.isNotEmpty()) {
        expectedAvionIds.forEachIndexed { index, expectedId ->
            spec.jsonPath("$.avions[$index].id").isEqualTo(expectedId)
        }
    }
}


fun WebTestClient.getHangar(id: Long): HangarResponseDTO {
    return this.get()
        .uri("/api/internal-AWY/hangars/$id")
        .exchange()
        .expectStatus().isOk
        .expectBody(HangarResponseDTO::class.java)
        .returnResult()
        .responseBody!!
}

/**
 * Получить все ангары
 */
fun WebTestClient.getAllHangars(): List<HangarResponseDTO> {
    return this.get()
        .uri("/api/internal-AWY/hangars")
        .exchange()
        .expectStatus().isOk
        .expectBodyList(HangarResponseDTO::class.java)
        .returnResult()
        .responseBody!!
}


fun WebTestClient.verifyBadRequestMessageForHangar(
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
