package fr.uga.miage.m1.utils

import fr.uga.miage.m1.Application.responseDTO.AvionResponseDTO
import fr.uga.miage.m1.Domain.updateRequest.UpdateAvionRequest
import fr.uga.miage.m1.Application.requestDTO.AvionRequestDTO
import org.hamcrest.Matchers
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.http.MediaType
fun WebTestClient.createAvion(request: AvionRequestDTO): AvionResponseDTO {
    return this.post()
        .uri("/api/internal-AWY/avions")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(request)
        .exchange()
        .expectStatus().isOk
        .expectBody(AvionResponseDTO::class.java)
        .returnResult()
        .responseBody!!
}

fun WebTestClient.deleteAvion(id: Long) {
    this.delete()
        .uri("/api/internal-AWY/avions/$id")
        .exchange()
        .expectStatus().isOk
}

fun WebTestClient.verifyAvionDeleted(id: Long) {
    this.get()
        .uri("/api/internal-AWY/avions/$id")
        .exchange()
        .expectStatus().isNotFound
}

fun WebTestClient.updateAvion(id: Long, update: UpdateAvionRequest) {
    this.put()
        .uri("/api/internal-AWY/avions/$id")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(update)
        .exchange()
        .expectStatus().isOk
}


fun WebTestClient.verifyAvionByImmatricule(
    numImmatricule: String,
    expectedNom: String
) {
    this.get()
        .uri("/api/internal-AWY/avions/numImat/$numImmatricule")
        .exchange()
        .expectStatus().isOk
        .expectBody()
        .jsonPath("$.nom").isEqualTo(expectedNom)
}

fun WebTestClient.searchAvionByImmatricule(numImmatricule: String): AvionResponseDTO {
    return this.get()
        .uri("/api/internal-AWY/avions/numImat/$numImmatricule")
        .exchange()
        .expectStatus().isOk
        .expectBody(AvionResponseDTO::class.java)
        .returnResult()
        .responseBody!!
}

fun WebTestClient.verifyAvionByImmatriculNotFound(numImmatricule: String) {
    this.get()
        .uri("/api/internal-AWY/avions/numImat/$numImmatricule")
        .exchange()
        .expectStatus().isNotFound
}

fun verifyAvionJson(
    client: WebTestClient,
    id: Long,
    expectedNom: String,
    expectedImmatricule: String,
    expectedType: String,
    expectedCapacite: Int,
    expectedEtat: String,
    expectedHangarId: Long? = null
) {
    val spec = client.get()
        .uri("/api/internal-AWY/avions/$id")
        .exchange()
        .expectStatus().isOk
        .expectBody()

    spec.jsonPath("$.nom").isEqualTo(expectedNom)
    spec.jsonPath("$.numImmatricule").isEqualTo(expectedImmatricule)
    spec.jsonPath("$.type").isEqualTo(expectedType)
    spec.jsonPath("$.capacite").isEqualTo(expectedCapacite)
    spec.jsonPath("$.etat").isEqualTo(expectedEtat)


    if (expectedHangarId != null) {
        spec.jsonPath("$.hangarId").isEqualTo(expectedHangarId)
    } else {
        spec.jsonPath("$.hangarId").doesNotExist()
    }
}

fun WebTestClient.getAvion(id: Long): AvionResponseDTO {
    return this.get()
        .uri("/api/internal-AWY/avions/$id")
        .exchange()
        .expectStatus().isOk
        .expectBody(AvionResponseDTO::class.java)
        .returnResult()
        .responseBody!!
    }

fun WebTestClient.getAllAvions(): List<AvionResponseDTO> {
    return this.get()
        .uri("/api/internal-AWY/avions")
        .exchange()
        .expectStatus().isOk
        .expectBodyList(AvionResponseDTO::class.java)
        .returnResult()
        .responseBody!!
}



// ------------------------------------------------
fun WebTestClient.verifyEntityCountAtLeast(endpoint: String, minCount: Int) {
    this.get()
        .uri(endpoint)
        .exchange()
        .expectStatus().isOk
        .expectBody()
        .jsonPath("$.length()").value<Int> { count ->
            assert(count >= minCount) {
                "Expected at least $minCount items at $endpoint, but got $count"
            }
        }
}

fun WebTestClient.verifyBadRequestMessage(
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


