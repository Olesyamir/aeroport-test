package fr.uga.miage.m1.utils

import fr.uga.miage.m1.Application.aeroportCentrale.CentralAirport
import fr.uga.miage.m1.Application.requestDTO.AvionRequestDTO
import fr.uga.miage.m1.Application.requestDTO.PisteRequestDTO
import fr.uga.miage.m1.Application.requestDTO.VolRequestDTO
import fr.uga.miage.m1.Application.responseDTO.VolResponseDTO
import fr.uga.miage.m1.Domain.enums.Etat
import fr.uga.miage.m1.Domain.enums.EtatMateriel
import fr.uga.miage.m1.Domain.enums.Statut
import fr.uga.miage.m1.Domain.updateRequest.UpdateVolRequest
import fr.uga.miage.m1.repository.AeroportRepository
import org.hamcrest.Matchers
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient



fun WebTestClient.createVol(request: VolRequestDTO): VolResponseDTO {
    return this.post()
        .uri("/api/vols")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(request)
        .exchange()
        .expectStatus().isOk
        .expectBody(VolResponseDTO::class.java)
        .returnResult()
        .responseBody!!
}

fun WebTestClient.getVol(id: Long): VolResponseDTO {
    return this.get()
        .uri("/api/vols/$id")
        .exchange()
        .expectStatus().isOk
        .expectBody(VolResponseDTO::class.java)
        .returnResult()
        .responseBody!!
}

fun WebTestClient.getAllVols(): List<VolResponseDTO> {
    return this.get()
        .uri("/api/vols")
        .exchange()
        .expectStatus().isOk
        .expectBodyList(VolResponseDTO::class.java)
        .returnResult()
        .responseBody!!
}

fun WebTestClient.updateVol(id: Long, request: UpdateVolRequest): VolResponseDTO {
    return this.put()
        .uri("/api/vols/$id")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(request)
        .exchange()
        .expectStatus().isOk
        .expectBody(VolResponseDTO::class.java)
        .returnResult()
        .responseBody!!
}

fun WebTestClient.deleteVol(id: Long) {
    this.delete()
        .uri("/api/vols/$id")
        .exchange()
        .expectStatus().isOk
}

fun WebTestClient.verifyVolDeleted(id: Long) {
    this.get()
        .uri("/api/vols/$id")
        .exchange()
        .expectStatus().isNotFound
}

fun WebTestClient.searchVolByNumero(numeroVol: String): VolResponseDTO {
    return this.get()
        .uri("/api/vols/numero/$numeroVol")
        .exchange()
        .expectStatus().isOk
        .expectBody(VolResponseDTO::class.java)
        .returnResult()
        .responseBody!!
}

fun WebTestClient.verifyVolNotFound(numeroVol: String) {
    this.get()
        .uri("/api/vols/numero/$numeroVol")
        .exchange()
        .expectStatus().isNotFound
}

fun WebTestClient.getVolsByStatut(statut: Statut): List<VolResponseDTO> {
    return this.get()
        .uri("/api/vols/statut/$statut")
        .exchange()
        .expectStatus().isOk
        .expectBodyList(VolResponseDTO::class.java)
        .returnResult()
        .responseBody!!
}

fun WebTestClient.getStatutById(id: Long): Statut {
    return this.get()
        .uri("/api/vols/$id/statut")
        .exchange()
        .expectStatus().isOk
        .expectBody(Statut::class.java)
        .returnResult()
        .responseBody!!
}

// ==================== VERIFICATION FUNCTIONS ====================

fun verifyVolJson(
    client: WebTestClient,
    id: Long,
    expectedNumero: String,
    expectedCompagnie: String,
    expectedOrigineId: Long?,
    expectedDestinationId: Long?,
    expectedStatut: String,
    expectedTypeVol: String,
    expectedAvionId: Long? = null,
    expectedPisteDecollageId: Long? = null,
    expectedPisteAtterissageId: Long? = null
) {
    val spec = client.get()
        .uri("/api/vols/$id")
        .exchange()
        .expectStatus().isOk
        .expectBody()

    spec.jsonPath("$.numeroVol").isEqualTo(expectedNumero)
    spec.jsonPath("$.compagnie").isEqualTo(expectedCompagnie)
    spec.jsonPath("$.statut").isEqualTo(expectedStatut)
    spec.jsonPath("$.typeVol").isEqualTo(expectedTypeVol)

    if (expectedOrigineId != null) {
        spec.jsonPath("$.origineId").isEqualTo(expectedOrigineId)
    } else {
        spec.jsonPath("$.origineId").doesNotExist()
    }

    if (expectedDestinationId != null) {
        spec.jsonPath("$.destinationId").isEqualTo(expectedDestinationId)
    } else {
        spec.jsonPath("$.destinationId").doesNotExist()
    }

    if (expectedAvionId != null) {
        spec.jsonPath("$.avionId").isEqualTo(expectedAvionId)
    } else {
        spec.jsonPath("$.avionId").doesNotExist()
    }

    if (expectedPisteDecollageId != null) {
        spec.jsonPath("$.pisteDecollageId").isEqualTo(expectedPisteDecollageId)
    } else {
        spec.jsonPath("$.pisteDecollageId").doesNotExist()
    }

    if (expectedPisteAtterissageId != null) {
        spec.jsonPath("$.pisteAtterissageId").isEqualTo(expectedPisteAtterissageId)
    } else {
        spec.jsonPath("$.pisteAtterissageId").doesNotExist()
    }
}

fun WebTestClient.verifyBadRequestMessageForVol(
    requestBody: Any,
    expectedMessagePart: String
) {
    this.post()
        .uri("/api/vols")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(requestBody)
        .exchange()
        .expectStatus().isBadRequest
        .expectBody()
        .jsonPath("$.message").value(Matchers.containsString(expectedMessagePart))
}

/**
 * Helper function to get the central airport ID from repository
 */
fun AeroportRepository.getCentralAirportId(): Long {
    return this.findByCodeIATA(CentralAirport.CODE_IATA)?.id
        ?: throw IllegalStateException("Central airport ${CentralAirport.CODE_IATA} not found")
}