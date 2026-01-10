package fr.uga.miage.m1.Application.endpoints

import fr.uga.miage.m1.Domain.updateRequest.UpdateHangarRequest
import fr.uga.miage.m1.Application.responseDTO.HangarResponseDTO
import fr.uga.miage.m1.Application.requestDTO.HangarRequestDTO
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema


@RequestMapping("/api/internal-AWY/hangars")
@Tag(name = "Hangar", description = "API de gestion des hangars")
interface HangarEndpoint {

    @Operation(
        summary = "Récupérer un hangar par ID",
        description = "Retourne les détails d'un hangar spécifique"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Hangar trouvé",
            content = [Content(schema = Schema(implementation = HangarResponseDTO::class))]
        ),
        ApiResponse(
            responseCode = "404",
            description = "Hangar non trouvé",
            content = [Content()]
        )
    ])
    @GetMapping("/{id}")
    fun getHangarById(
        @Parameter(description = "ID du hangar", required = true)
        @PathVariable id: Long
    ): HangarResponseDTO

    @Operation(
        summary = "Récupérer tous les hangars",
        description = "Retourne la liste complète de tous les hangars disponibles"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Liste des hangars récupérée avec succès",
            content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = HangarResponseDTO::class)
            )]
        )
    ])
    @GetMapping
    fun getAllHangars(): Collection<HangarResponseDTO>

    @Operation(
        summary = "Créer un nouveau hangar",
        description = "Crée un nouveau hangar avec les informations fournies"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "201",
            description = "Hangar créé avec succès",
            content = [Content(schema = Schema(implementation = HangarResponseDTO::class))]
        ),
        ApiResponse(
            responseCode = "400",
            description = "Données invalides",
            content = [Content()]
        )
    ])
    @PostMapping
    fun createHangar(
        @Parameter(description = "Données du hangar à créer", required = true)
        @RequestBody request: HangarRequestDTO
    ): HangarResponseDTO

    @Operation(
        summary = "Mettre à jour un hangar",
        description = "Met à jour les informations d'un hangar existant"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Hangar mis à jour avec succès",
            content = [Content(schema = Schema(implementation = HangarResponseDTO::class))]
        ),
        ApiResponse(
            responseCode = "404",
            description = "Hangar non trouvé"
        ),
        ApiResponse(
            responseCode = "400",
            description = "Données invalides"
        )
    ])
    @PutMapping("/{id}")
    fun updateHangar(
        @Parameter(description = "ID du hangar à mettre à jour", required = true)
        @PathVariable id: Long,
        @Parameter(description = "Nouvelles données du hangar", required = true)
        @RequestBody updateRequest: UpdateHangarRequest
    ): HangarResponseDTO

    @Operation(
        summary = "Supprimer un hangar",
        description = "Supprime un hangar en utilisant son ID"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "204",
            description = "Hangar supprimé avec succès"
        ),
        ApiResponse(
            responseCode = "404",
            description = "Hangar non trouvé"
        )
    ])
    @DeleteMapping("/{id}")
    fun deleteHangar(
        @Parameter(description = "ID du hangar à supprimer", required = true)
        @PathVariable id: Long
    )

}
