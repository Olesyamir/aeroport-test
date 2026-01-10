package fr.uga.miage.m1.utils

import fr.uga.miage.m1.Application.responseDTO.PlanningPistesResponseDTO
import fr.uga.miage.m1.Application.requestDTO.PlanningPistesRequestDTO
import fr.uga.miage.m1.Domain.enums.Priorite
import fr.uga.miage.m1.Domain.enums.Usage
import fr.uga.miage.m1.Domain.updateRequest.UpdatePlanningPistesRequest
import org.hamcrest.Matchers
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.LocalTime
import org.junit.jupiter.api.Assertions.assertEquals

fun WebTestClient.createPlanningPiste(request: PlanningPistesRequestDTO): PlanningPistesResponseDTO =
    this.post()
        .uri("/api/internal-AWY/planningPistes")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(request)
        .exchange()
        .expectStatus().isOk
        .expectBody(PlanningPistesResponseDTO::class.java)
        .returnResult()
        .responseBody!!

fun WebTestClient.getPlanningPiste(id: Long): PlanningPistesResponseDTO {
    return this.get()
        .uri("/api/internal-AWY/planningPistes/$id")
        .exchange()
        .expectStatus().isOk
        .expectBody(PlanningPistesResponseDTO::class.java)
        .returnResult()
        .responseBody!!
}

fun WebTestClient.getAllPlanningPistes(): List<PlanningPistesResponseDTO> {
    return this.get()
        .uri("/api/internal-AWY/planningPistes")
        .exchange()
        .expectStatus().isOk
        .expectBodyList(PlanningPistesResponseDTO::class.java)
        .returnResult()
        .responseBody!!
}

fun WebTestClient.deletePlanningPiste(id: Long) {
    this.delete()
        .uri("/api/internal-AWY/planningPistes/$id")
        .exchange()
        .expectStatus().isOk
}

fun WebTestClient.verifyPlanningPisteDeleted(id: Long) {
    this.get()
        .uri("/api/internal-AWY/planningPistes/$id")
        .exchange()
        .expectStatus().isNotFound
}

fun WebTestClient.updatePlanningPiste(id: Long, update: UpdatePlanningPistesRequest) {
    this.put()
        .uri("/api/internal-AWY/planningPistes/$id")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(update)
        .exchange()
        .expectStatus().isOk
}


fun WebTestClient.verifyPlanningPisteJson(
    id: Long,
    expectedStartTime: LocalTime,
    expectedEndTime: LocalTime,
    expectedPriorite: Priorite,
    expectedUsage: Usage,
    expectedPisteId: Long? = null,
    expectedVolsCount: Int? = null
) {
    val spec = this.get()
        .uri("/api/internal-AWY/planningPistes/$id")
        .exchange()
        .expectStatus().isOk
        .expectBody()
        .jsonPath("$.startTime").value<String> { actual ->
            assertEquals(expectedStartTime.toString(), LocalTime.parse(actual).withSecond(0).toString())
        }
        .jsonPath("$.endTime").value<String> { actual ->
            assertEquals(expectedEndTime.toString(), LocalTime.parse(actual).withSecond(0).toString())
        }
        .jsonPath("$.priorite").isEqualTo(expectedPriorite.toString())
        .jsonPath("$.usage").isEqualTo(expectedUsage.toString())

    if (expectedPisteId != null) {
        spec.jsonPath("$.pisteId").isEqualTo(expectedPisteId)
    }

    if (expectedVolsCount != null) {
        val response = this.get()
            .uri("/api/internal-AWY/planningPistes/$id")
            .exchange()
            .expectStatus().isOk
            .expectBody(PlanningPistesResponseDTO::class.java)
            .returnResult()
            .responseBody!!

        val totalVols = (response.volsDepartId?.size ?: 0) + (response.volsArriveeId?.size ?: 0)
        assertEquals(expectedVolsCount, totalVols, "Expected $expectedVolsCount vols but got $totalVols")
    }
}


fun WebTestClient.verifyBadRequestMessageForPlanningPiste(
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

