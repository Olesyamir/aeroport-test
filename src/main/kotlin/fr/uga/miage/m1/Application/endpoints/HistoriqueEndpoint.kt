package fr.uga.miage.m1.Application.endpoints

import fr.uga.miage.m1.Application.responseDTO.HistoriqueResponseDTO
import fr.uga.miage.m1.Application.requestDTO.HistoriqueRequestDTO
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag


@RequestMapping("/api/internal-AWY/historiques")
@Tag(name = "Historique", description = "API de gestion des historiques")
interface HistoriqueEndpoint {

    @Operation(
        summary = "Récupérer tous les historiques",
        description = "Retourne la liste complète de tous les historiques disponibles"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Liste des historiques récupérée avec succès",
            content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = HistoriqueResponseDTO::class)
            )]
        )
    ])
    @GetMapping
    fun getAllHistorique(): Collection<HistoriqueResponseDTO>

    @Operation(
        summary = "Récupérer un historique par ID",
        description = "Retourne les détails d'un historique spécifique"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Historique trouvé",
            content = [Content(schema = Schema(implementation = HistoriqueResponseDTO::class))]
        ),
        ApiResponse(responseCode = "404", description = "Historique non trouvé")
    ])
    @GetMapping("/{id}")
    fun getHistoriqueById(@PathVariable id : Long): HistoriqueResponseDTO

    @Operation(
        summary = "Créer un nouvel historique",
        description = "Crée un nouvel historique avec les informations fournies"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "201",
            description = "Historique créé avec succès",
            content = [Content(schema = Schema(implementation = HistoriqueResponseDTO::class))]
        ),
        ApiResponse(responseCode = "400", description = "Données invalides")
    ])
    @PostMapping
    fun createHistorique(@RequestBody createHistorique : HistoriqueRequestDTO): HistoriqueResponseDTO

    @Operation(
        summary = "Rechercher un historique par ID de vol",
        description = "Retourne l'historique associé à un vol spécifique"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Historique trouvé",
            content = [Content(schema = Schema(implementation = HistoriqueResponseDTO::class))]
        ),
        ApiResponse(responseCode = "404", description = "Historique non trouvé")
    ])
    @GetMapping("/idVol/{idVol}")
    fun searchByIDVol(@PathVariable idVol : Long) : HistoriqueResponseDTO

    @Operation(
        summary = "Supprimer un historique",
        description = "Supprime un historique en utilisant son ID"
    )
    @ApiResponses(value = [
        ApiResponse(responseCode = "204", description = "Historique supprimé avec succès"),
        ApiResponse(responseCode = "404", description = "Historique non trouvé")
    ])
    @DeleteMapping("/{id}")
    fun deleteHistoriqueById(@PathVariable id : Long)
}
