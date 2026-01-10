package fr.uga.miage.m1.Application.endpoints

import fr.uga.miage.m1.Domain.updateRequest.UpdateVolRequest
import fr.uga.miage.m1.Domain.enums.Statut
import fr.uga.miage.m1.Application.responseDTO.VolResponseDTO
import fr.uga.miage.m1.Application.requestDTO.VolRequestDTO
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

@RequestMapping("/api/vols")
@Tag(name = "Vol", description = "API de gestion des vols")
interface VolEndpoint {

    @Operation(
        summary = "Récupérer tous les vols",
        description = "Retourne la liste complète de tous les vols disponibles"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Liste des vols récupérée avec succès",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = VolResponseDTO::class)
                )]
            )
        ]
    )
    @GetMapping
    fun getAllVols(): Collection<VolResponseDTO>

    @Operation(
        summary = "Récupérer un vol par ID",
        description = "Retourne les détails d'un vol spécifique"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Vol trouvé",
                content = [Content(schema = Schema(implementation = VolResponseDTO::class))]
            ),
            ApiResponse(responseCode = "404", description = "Vol non trouvé")
        ]
    )
    @GetMapping("/{id}")
    fun getVolById(@PathVariable id: Long): VolResponseDTO

    @Operation(
        summary = "Créer un nouveau vol",
        description = "Crée un nouveau vol avec les informations fournies"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "Vol créé avec succès",
                content = [Content(schema = Schema(implementation = VolResponseDTO::class))]
            ),
            ApiResponse(responseCode = "400", description = "Données invalides")
        ]
    )
    @PostMapping
    fun createVol(@RequestBody entity: VolRequestDTO): VolResponseDTO

    @Operation(
        summary = "Supprimer un vol",
        description = "Supprime un vol en utilisant son ID"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "Vol supprimé avec succès"),
            ApiResponse(responseCode = "404", description = "Vol non trouvé")
        ]
    )
    @DeleteMapping("/{id}")
    fun deleteVolById(@PathVariable id: Long)

    @Operation(
        summary = "Mettre à jour un vol",
        description = "Met à jour les informations d'un vol existant"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Vol mis à jour avec succès",
                content = [Content(schema = Schema(implementation = VolResponseDTO::class))]
            ),
            ApiResponse(responseCode = "404", description = "Vol non trouvé"),
            ApiResponse(responseCode = "400", description = "Données invalides")
        ]
    )
    @PutMapping("/{id}")
    fun updateVolById(
        @PathVariable id: Long,
        @RequestBody updateVol: UpdateVolRequest
    ): VolResponseDTO

    @Operation(
        summary = "Rechercher un vol par numéro",
        description = "Retourne un vol en utilisant son numéro"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Vol trouvé",
                content = [Content(schema = Schema(implementation = VolResponseDTO::class))]
            ),
            ApiResponse(responseCode = "404", description = "Vol non trouvé")
        ]
    )
    @GetMapping("/numero/{numeroVol}")
    fun searchNum(@PathVariable numeroVol: String): VolResponseDTO?

    @Operation(
        summary = "Lister les vols par statut",
        description = "Retourne la liste des vols filtrés par statut"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Liste des vols par statut récupérée avec succès",
                content = [Content(schema = Schema(implementation = VolResponseDTO::class))]
            )
        ]
    )
    @GetMapping("/statut/{statut}")
    fun listesVolParStatut(@PathVariable statut: Statut): Collection<VolResponseDTO>

    @Operation(
        summary = "Récupérer le statut d'un vol par ID",
        description = "Retourne uniquement le statut d'un vol spécifique"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Statut du vol trouvé",
                content = [Content(schema = Schema(implementation = Statut::class))]
            ),
            ApiResponse(responseCode = "404", description = "Vol non trouvé")
        ]
    )
    @GetMapping("/{id}/statut")
    fun findStatutById(@PathVariable id: Long): Statut?
}

