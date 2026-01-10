package fr.uga.miage.m1.utils

import fr.uga.miage.m1.Application.requestDTO.HistoriqueRequestDTO
import fr.uga.miage.m1.Application.responseDTO.HistoriqueResponseDTO
import fr.uga.miage.m1.Domain.enums.Statut
import org.hamcrest.Matchers
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.LocalDateTime


fun WebTestClient.createHistorique(request: HistoriqueRequestDTO): HistoriqueResponseDTO {
    return this.post()
        .uri("/api/internal-AWY/historiques")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(request)
        .exchange()
        .expectStatus().isOk
        .expectBody(HistoriqueResponseDTO::class.java)
        .returnResult()
        .responseBody!!
}

fun WebTestClient.getHistorique(id: Long): HistoriqueResponseDTO {
    return this.get()
        .uri("/api/internal-AWY/historiques/$id")
        .exchange()
        .expectStatus().isOk
        .expectBody(HistoriqueResponseDTO::class.java)
        .returnResult()
        .responseBody!!
}

fun WebTestClient.getAllHistoriques(): List<HistoriqueResponseDTO> {
    return this.get()
        .uri("/api/internal-AWY/historiques")
        .exchange()
        .expectStatus().isOk
        .expectBodyList(HistoriqueResponseDTO::class.java)
        .returnResult()
        .responseBody!!
}

fun WebTestClient.searchHistoriqueByVolId(volId: Long): HistoriqueResponseDTO {
    return this.get()
        .uri("/api/internal-AWY/historiques/idVol/$volId")
        .exchange()
        .expectStatus().isOk
        .expectBody(HistoriqueResponseDTO::class.java)
        .returnResult()
        .responseBody!!
}

fun WebTestClient.verifyHistoriqueNotFoundByVolId(volId: Long) {
    this.get()
        .uri("/api/internal-AWY/historiques/idVol/$volId")
        .exchange()
        .expectStatus().isNotFound
}

fun WebTestClient.deleteHistorique(id: Long) {
    this.delete()
        .uri("/api/internal-AWY/historiques/$id")
        .exchange()
        .expectStatus().isOk
}

fun WebTestClient.verifyHistoriqueDeleted(id: Long) {
    this.get()
        .uri("/api/internal-AWY/historiques/$id")
        .exchange()
        .expectStatus().isNotFound
}

// ==================== VERIFICATION FUNCTIONS ====================

fun verifyHistoriqueJson(
    client: WebTestClient,
    id: Long,
    expectedStatut: String,
    expectedVolId: Long?
) {
    val spec = client.get()
        .uri("/api/internal-AWY/historiques/$id")
        .exchange()
        .expectStatus().isOk
        .expectBody()

    spec.jsonPath("$.statut").isEqualTo(expectedStatut)
    spec.jsonPath("$.datetime").exists()

    if (expectedVolId != null) {
        spec.jsonPath("$.idVol").isEqualTo(expectedVolId)
    } else {
        spec.jsonPath("$.idVol").doesNotExist()
    }
}

fun WebTestClient.verifyBadRequestMessageForHistorique(
    requestBody: Any,
    expectedMessagePart: String
) {
    this.post()
        .uri("/api/internal-AWY/historiques")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(requestBody)
        .exchange()
        .expectStatus().isBadRequest
        .expectBody()
        .jsonPath("$.message").value(Matchers.containsString(expectedMessagePart))
}