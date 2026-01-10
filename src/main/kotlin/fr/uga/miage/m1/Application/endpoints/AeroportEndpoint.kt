package fr.uga.miage.m1.endpoints

import fr.uga.miage.m1.reponses.AeroportResponseDTO
import fr.uga.miage.m1.request.AeroportRequestDTO
import fr.uga.miage.m1.updateRequest.UpdateAeroportRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*

@RequestMapping("/api/internal-AWY/aeroports")
@Tag(name = "Aeroport", description = "API de gestion des aéroports")
interface AeroportEndpoint {

    @Operation(
        summary = "Récupérer un aéroport par ID",
        description = "Retourne les détails d'un aéroport spécifique"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Aéroport trouvé",
            content = [Content(schema = Schema(implementation = AeroportResponseDTO::class))]
        ),
        ApiResponse(
            responseCode = "404",
            description = "Aéroport non trouvé",
            content = [Content()]
        )
    ])
    @GetMapping("/{id}")
    fun getAeroportById(
        @Parameter(description = "ID de l'aéroport", required = true)
        @PathVariable id: Long
    ): AeroportResponseDTO

    @Operation(
        summary = "Récupérer tous les aéroports",
        description = "Retourne la liste complète de tous les aéroports disponibles"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Liste des aéroports récupérée avec succès",
            content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = AeroportResponseDTO::class)
            )]
        )
    ])
    @GetMapping
    fun getAllAeroports(): Collection<AeroportResponseDTO>

    @Operation(
        summary = "Créer un nouveau aéroport",
        description = "Crée un nouvel aéroport avec les informations fournies"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "201",
            description = "Aéroport créé avec succès",
            content = [Content(schema = Schema(implementation = AeroportResponseDTO::class))]
        ),
        ApiResponse(
            responseCode = "400",
            description = "Données invalides",
            content = [Content()]
        ),
        ApiResponse(
            responseCode = "409",
            description = "Un aéroport avec ce code IATA existe déjà",
            content = [Content()]
        )
    ])
    @PostMapping
    fun createAeroport(
        @Parameter(description = "Données de l'aéroport à créer", required = true)
        @RequestBody request: AeroportRequestDTO
    ): AeroportResponseDTO

    @Operation(
        summary = "Mettre à jour un aéroport",
        description = "Met à jour les informations d'un aéroport existant"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Aéroport mis à jour avec succès",
            content = [Content(schema = Schema(implementation = AeroportResponseDTO::class))]
        ),
        ApiResponse(
            responseCode = "404",
            description = "Aéroport non trouvé"
        ),
        ApiResponse(
            responseCode = "400",
            description = "Données invalides"
        ),
        ApiResponse(
            responseCode = "409",
            description = "Le code IATA est déjà utilisé par un autre aéroport"
        )
    ])
    @PutMapping("/{id}")
    fun updateAeroport(
        @Parameter(description = "ID de l'aéroport à mettre à jour", required = true)
        @PathVariable id: Long,
        @Parameter(description = "Nouvelles données de l'aéroport", required = true)
        @RequestBody updateRequest: UpdateAeroportRequest
    ): AeroportResponseDTO

    @Operation(
        summary = "Supprimer un aéroport",
        description = "Supprime un aéroport en utilisant son ID"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "204",
            description = "Aéroport supprimé avec succès"
        ),
        ApiResponse(
            responseCode = "404",
            description = "Aéroport non trouvé"
        )
    ])
    @DeleteMapping("/{id}")
    fun deleteAeroport(
        @Parameter(description = "ID de l'aéroport à supprimer", required = true)
        @PathVariable id: Long
    )

    @Operation(
        summary = "Rechercher un aéroport par code IATA",
        description = "Retourne un aéroport en utilisant son code IATA (3 lettres)"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Aéroport trouvé",
            content = [Content(schema = Schema(implementation = AeroportResponseDTO::class))]
        ),
        ApiResponse(
            responseCode = "404",
            description = "Aéroport non trouvé"
        )
    ])
    @GetMapping("/code/{codeIATA}")
    fun searchByCodeIATA(
        @Parameter(description = "Code IATA de l'aéroport (3 lettres)", required = true)
        @PathVariable codeIATA: String
    ): AeroportResponseDTO?

    @Operation(
        summary = "Récupérer les vols au départ",
        description = "Retourne la liste des IDs des vols au départ pour un aéroport"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Liste des IDs des vols au départ récupérée avec succès"
        ),
        ApiResponse(
            responseCode = "404",
            description = "Aéroport non trouvé"
        )
    ])
    @GetMapping("/{id}/vols-depart")
    fun getVolsDepart(
        @Parameter(description = "ID de l'aéroport", required = true)
        @PathVariable id: Long
    ): Collection<Long>

    @Operation(
        summary = "Récupérer les vols à l'arrivée",
        description = "Retourne la liste des IDs des vols à l'arrivée pour un aéroport"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Liste des IDs des vols à l'arrivée récupérée avec succès"
        ),
        ApiResponse(
            responseCode = "404",
            description = "Aéroport non trouvé"
        )
    ])
    @GetMapping("/{id}/vols-arrivee")
    fun getVolsArrivee(
        @Parameter(description = "ID de l'aéroport", required = true)
        @PathVariable id: Long
    ): Collection<Long>
}
