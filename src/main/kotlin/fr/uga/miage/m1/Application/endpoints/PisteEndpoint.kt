package fr.uga.miage.m1.Application.endpoints

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import fr.uga.miage.m1.Domain.updateRequest.UpdatePisteRequest
import fr.uga.miage.m1.Application.responseDTO.PisteResponseDTO
import fr.uga.miage.m1.Application.requestDTO.PisteRequestDTO
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping

@RequestMapping("/api/internal-AWY/pistes")
@Tag(name = "Piste", description = "API de gestion des pistes")
interface PisteEndpoint {

    @Operation(
        summary = "Récupérer toutes les pistes",
        description = "Retourne la liste complète de toutes les pistes disponibles"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Liste des pistes récupérée avec succès",
            content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = PisteResponseDTO::class)
            )]
        )
    ])
    @GetMapping
    fun getAllPiste() : Collection<PisteResponseDTO>

    @Operation(
        summary = "Récupérer une piste par ID",
        description = "Retourne les détails d'une piste spécifique"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Piste trouvée",
            content = [Content(schema = Schema(implementation = PisteResponseDTO::class))]
        ),
        ApiResponse(responseCode = "404", description = "Piste non trouvée")
    ])
    @GetMapping("/{id}")
    fun getPisteById(@PathVariable id:Long) : PisteResponseDTO

    @Operation(
        summary = "Créer une nouvelle piste",
        description = "Crée une nouvelle piste avec les informations fournies"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "201",
            description = "Piste créée avec succès",
            content = [Content(schema = Schema(implementation = PisteResponseDTO::class))]
        ),
        ApiResponse(responseCode = "400", description = "Données invalides")
    ])
    @PostMapping
    fun createPiste(@RequestBody pisteRequestDTO: PisteRequestDTO) : PisteResponseDTO

    @Operation(
        summary = "Mettre à jour l'état d'une piste",
        description = "Met à jour les informations d'une piste existante"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Piste mise à jour avec succès",
            content = [Content(schema = Schema(implementation = PisteResponseDTO::class))]
        ),
        ApiResponse(responseCode = "404", description = "Piste non trouvée"),
        ApiResponse(responseCode = "400", description = "Données invalides")
    ])
    @PutMapping("/{id}")
    fun updateEtatPiste(
        @PathVariable id:Long,
        @RequestBody updatePisteRequest: UpdatePisteRequest
    ): PisteResponseDTO

    @Operation(
        summary = "Supprimer une piste",
        description = "Supprime une piste en utilisant son ID"
    )
    @ApiResponses(value = [
        ApiResponse(responseCode = "204", description = "Piste supprimée avec succès"),
        ApiResponse(responseCode = "404", description = "Piste non trouvée")
    ])
    @DeleteMapping("/{id}")
    fun deletePiste(@PathVariable id:Long)


}
