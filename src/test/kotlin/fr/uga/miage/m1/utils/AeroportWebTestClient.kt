package fr.uga.miage.m1.utils

import fr.uga.miage.m1.reponses.AeroportResponseDTO
import fr.uga.miage.m1.request.AeroportRequestDTO
import fr.uga.miage.m1.updateRequest.UpdateAeroportRequest
import org.hamcrest.Matchers
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient


fun WebTestClient.createAeroport(request: AeroportRequestDTO): AeroportResponseDTO {
    return this.post()
        .uri("/api/internal-AWY/aeroports")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(request)
        .exchange()
        .expectStatus().isOk
        .expectBody(AeroportResponseDTO::class.java)
        .returnResult()
        .responseBody!!
}


fun WebTestClient.getAeroport(id: Long): AeroportResponseDTO {
    return this.get()
        .uri("/api/internal-AWY/aeroports/$id")
        .exchange()
        .expectStatus().isOk
        .expectBody(AeroportResponseDTO::class.java)
        .returnResult()
        .responseBody!!
}


fun WebTestClient.getAllAeroports(): List<AeroportResponseDTO> {
    return this.get()
        .uri("/api/internal-AWY/aeroports")
        .exchange()
        .expectStatus().isOk
        .expectBodyList(AeroportResponseDTO::class.java)
        .returnResult()
        .responseBody!!
}


fun WebTestClient.deleteAeroport(id: Long) {
    this.delete()
        .uri("/api/internal-AWY/aeroports/$id")
        .exchange()
        .expectStatus().isOk
}


fun WebTestClient.verifyAeroportDeleted(id: Long) {
    this.get()
        .uri("/api/internal-AWY/aeroports/$id")
        .exchange()
        .expectStatus().isNotFound
}


fun WebTestClient.updateAeroport(id: Long, update: UpdateAeroportRequest): AeroportResponseDTO {
    return this.put()
        .uri("/api/internal-AWY/aeroports/$id")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(update)
        .exchange()
        .expectStatus().isOk
        .expectBody(AeroportResponseDTO::class.java)
        .returnResult()
        .responseBody!!
}


fun WebTestClient.searchAeroportByCodeIATA(codeIATA: String): AeroportResponseDTO? {
    return this.get()
        .uri("/api/internal-AWY/aeroports/code/$codeIATA")
        .exchange()
        .expectStatus().isOk
        .expectBody(AeroportResponseDTO::class.java)
        .returnResult()
        .responseBody
}


fun WebTestClient.getVolsDepart(aeroportId: Long): List<Long> {
    return this.get()
        .uri("/api/internal-AWY/aeroports/$aeroportId/vols-depart")
        .exchange()
        .expectStatus().isOk
        .expectBodyList(Long::class.java)
        .returnResult()
        .responseBody!!
}


fun WebTestClient.getVolsArrivee(aeroportId: Long): List<Long> {
    return this.get()
        .uri("/api/internal-AWY/aeroports/$aeroportId/vols-arrivee")
        .exchange()
        .expectStatus().isOk
        .expectBodyList(Long::class.java)
        .returnResult()
        .responseBody!!
}


fun WebTestClient.verifyAeroportJson(
    id: Long,
    expectedNom: String,
    expectedVille: String,
    expectedPays: String,
    expectedCodeIATA: String,
    expectedVolsDepartCount: Int? = null,
    expectedVolsArriveeCount: Int? = null,
    expectedPistesCount: Int? = null,
    expectedHangarsCount: Int? = null
) {
    val spec = this.get()
        .uri("/api/internal-AWY/aeroports/$id")
        .exchange()
        .expectStatus().isOk
        .expectBody()
        .jsonPath("$.nom").isEqualTo(expectedNom)
        .jsonPath("$.ville").isEqualTo(expectedVille)
        .jsonPath("$.pays").isEqualTo(expectedPays)
        .jsonPath("$.codeIATA").isEqualTo(expectedCodeIATA)

    if (expectedVolsDepartCount != null) {
        spec.jsonPath("$.volsDepartIds.length()").isEqualTo(expectedVolsDepartCount)
    }

    if (expectedVolsArriveeCount != null) {
        spec.jsonPath("$.volsArriveeIds.length()").isEqualTo(expectedVolsArriveeCount)
    }

    if (expectedPistesCount != null) {
        spec.jsonPath("$.pisteIds.length()").isEqualTo(expectedPistesCount)
    }

    if (expectedHangarsCount != null) {
        spec.jsonPath("$.hangarIds.length()").isEqualTo(expectedHangarsCount)
    }
}



// ---------------
fun WebTestClient.verifyBadRequest(
    uri: String,
    requestBody: Any,
    expectedMessagePart: String = "",
    expectedStatus: HttpStatus = HttpStatus.CONFLICT
) {
    this.post()
        .uri(uri)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(requestBody)
        .exchange()
        .expectStatus().isEqualTo(expectedStatus)
        .expectBody()
        .jsonPath("$.message").value(Matchers.containsString(expectedMessagePart))
}