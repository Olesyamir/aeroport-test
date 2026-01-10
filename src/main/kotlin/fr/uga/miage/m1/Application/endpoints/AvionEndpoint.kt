package fr.uga.miage.m1.Application.endpoints

import fr.uga.miage.m1.Domain.updateRequest.UpdateAvionRequest
import fr.uga.miage.m1.Application.responseDTO.AvionResponseDTO
import fr.uga.miage.m1.Application.requestDTO.AvionRequestDTO
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

@RequestMapping("/api/internal-AWY/avions")
@Tag(name = "Avion", description = "API de gestion des avions")
interface AvionEndpoint {
    @Operation(
        summary = "Récupérer tous les avions",
        description = "Retourne la liste complète de tous les avions disponibles"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Liste des avions récupérée avec succès",
            content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = AvionResponseDTO::class)
            )]
        )
    ])
    @GetMapping
    fun getAllAvions(): Collection<AvionResponseDTO>

    @Operation(
        summary = "Récupérer un avion par ID",
        description = "Retourne les détails d'un avion spécifique"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Avion trouvé",
            content = [Content(schema = Schema(implementation = AvionResponseDTO::class))]
        ),
        ApiResponse(
            responseCode = "404",
            description = "Avion non trouvé",
            content = [Content()]
        )
    ])
    @GetMapping("/{id}")
    fun getAvionById(@PathVariable id: Long) : AvionResponseDTO?

    @Operation(
        summary = "Créer un nouvel avion",
        description = "Crée un nouvel avion avec les informations fournies"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "201",
            description = "Avion créé avec succès",
            content = [Content(schema = Schema(implementation = AvionResponseDTO::class))]
        ),
        ApiResponse(
            responseCode = "400",
            description = "Données invalides",
            content = [Content()]
        )
    ])
    @PostMapping
    fun createAvion(@RequestBody createAvion: AvionRequestDTO): AvionResponseDTO

    @Operation(
        summary = "Supprimer un avion",
        description = "Supprime un avion en utilisant son ID"
    )
    @ApiResponses(value = [
        ApiResponse(responseCode = "204", description = "Avion supprimé avec succès"),
        ApiResponse(responseCode = "404", description = "Avion non trouvé")
    ])
    @DeleteMapping("/{id}")
    fun deleteAvionById(@PathVariable id: Long)

    @Operation(
        summary = "Mettre à jour un avion",
        description = "Met à jour les informations d'un avion existant"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Avion mis à jour avec succès",
            content = [Content(schema = Schema(implementation = AvionResponseDTO::class))]
        ),
        ApiResponse(responseCode = "404", description = "Avion non trouvé"),
        ApiResponse(responseCode = "400", description = "Données invalides")
    ])
    @PutMapping("/{id}")
    fun updateAvion(
        @PathVariable id: Long,
        @RequestBody updateAvion: UpdateAvionRequest): AvionResponseDTO

    @Operation(
        summary = "Rechercher un avion par numéro d'immatriculation",
        description = "Trouve un avion en utilisant son numéro d'immatriculation"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Avion trouvé",
            content = [Content(schema = Schema(implementation = AvionResponseDTO::class))]
        ),
        ApiResponse(responseCode = "404", description = "Avion non trouvé")
    ])
    @GetMapping("/numImat/{numImmatricule}")
    fun searchByNumImmatricule(@PathVariable numImmatricule: String): AvionResponseDTO ?
}
