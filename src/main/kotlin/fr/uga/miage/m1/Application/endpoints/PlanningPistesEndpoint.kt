package fr.uga.miage.m1.Application.endpoints

import fr.uga.miage.m1.Application.responseDTO.PlanningPistesResponseDTO
import fr.uga.miage.m1.Application.requestDTO.PlanningPistesRequestDTO
import fr.uga.miage.m1.Domain.updateRequest.UpdatePlanningPistesRequest
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag

@RequestMapping("/api/internal-AWY/planningPistes")
@Tag(name = "Planning Pistes", description = "API de gestion des plannings de pistes")
interface PlanningPistesEndpoint {

    @Operation(
        summary = "Récupérer tous les plannings de pistes",
        description = "Retourne la liste complète des plannings de pistes disponibles"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Liste des plannings récupérée avec succès",
            content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = PlanningPistesResponseDTO::class)
            )]
        )
    ])
    @GetMapping
    fun getAllPlanningPistes(): Collection<PlanningPistesResponseDTO>

    @GetMapping("/{id}")
    fun getPlanningPisteById(@PathVariable id: Long): PlanningPistesResponseDTO

    @Operation(
        summary = "Créer un nouveau planning de piste",
        description = "Crée un nouveau planning de piste avec les informations fournies"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "201",
            description = "Planning créé avec succès",
            content = [Content(schema = Schema(implementation = PlanningPistesResponseDTO::class))]
        ),
        ApiResponse(responseCode = "400", description = "Données invalides")
    ])
    @PostMapping
    fun createPlanningPiste(@RequestBody entity: PlanningPistesRequestDTO): PlanningPistesResponseDTO

    @Operation(
        summary = "Supprimer un planning de piste",
        description = "Supprime un planning de piste en utilisant son ID"
    )
    @ApiResponses(value = [
        ApiResponse(responseCode = "204", description = "Planning supprimé avec succès"),
        ApiResponse(responseCode = "404", description = "Planning non trouvé")
    ])
    @DeleteMapping("/{id}")
    fun deletePlanningPisteById(@PathVariable id: Long)

    @Operation(
        summary = "Mettre à jour un planning de piste",
        description = "Met à jour les informations d'un planning de piste existant"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Planning mis à jour avec succès",
            content = [Content(schema = Schema(implementation = PlanningPistesResponseDTO::class))]
        ),
        ApiResponse(responseCode = "404", description = "Planning non trouvé"),
        ApiResponse(responseCode = "400", description = "Données invalides")
    ])
    @PutMapping("/{id}")
    fun updatePlanningPisteById(
        @PathVariable id: Long,
        @RequestBody update : UpdatePlanningPistesRequest
    ): PlanningPistesResponseDTO
}
