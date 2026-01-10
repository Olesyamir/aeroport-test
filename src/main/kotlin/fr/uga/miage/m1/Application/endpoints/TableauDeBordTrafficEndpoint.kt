package fr.uga.miage.m1.endpoints

import fr.uga.miage.m1.Application.responseDTO.HistoriqueResponseDTO
import fr.uga.miage.m1.Application.responseDTO.PlanningPistesResponseDTO
import fr.uga.miage.m1.Domain.enums.Statut
import fr.uga.miage.m1.reponses.TableauDeBordTrafficDTO
import fr.uga.miage.m1.reponses.VolTrafficDTO
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*

@RequestMapping("/api/tableau-bord")
@Tag(name = "Tableau de Bord Traffic", description = "API de gestion du tableau de bord du trafic aérien")
interface TableauDeBordTrafficEndpoint {

    @Operation(
        summary = "Obtenir le tableau de bord complet",
        description = "Retourne une vue consolidée de tout le trafic de l'aéroport"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Tableau de bord récupéré avec succès",
            content = [Content(schema = Schema(implementation = TableauDeBordTrafficDTO::class))]
        ),
        ApiResponse(
            responseCode = "404",
            description = "Aéroport non trouvé",
            content = [Content()]
        )
    ])
    @GetMapping("/{aeroportId}")
    fun getTableauDeBord(
        @Parameter(description = "ID de l'aéroport", required = true)
        @PathVariable aeroportId: Long
    ): TableauDeBordTrafficDTO

    @Operation(
        summary = "Obtenir les vols au départ",
        description = "Retourne la liste de tous les vols au départ de l'aéroport"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Collectione des vols au départ récupérée avec succès"
        ),
        ApiResponse(
            responseCode = "404",
            description = "Aéroport non trouvé"
        )
    ])
    @GetMapping("/{aeroportId}/vols-depart")
    fun getVolsDepart(
        @Parameter(description = "ID de l'aéroport", required = true)
        @PathVariable aeroportId: Long
    ): Collection<VolTrafficDTO>

    @Operation(
        summary = "Obtenir les vols à l'arrivée",
        description = "Retourne la liste de tous les vols à l'arrivée dans l'aéroport"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Collectione des vols à l'arrivée récupérée avec succès"
        ),
        ApiResponse(
            responseCode = "404",
            description = "Aéroport non trouvé"
        )
    ])
    @GetMapping("/{aeroportId}/vols-arrivee")
    fun getVolsArrivee(
        @Parameter(description = "ID de l'aéroport", required = true)
        @PathVariable aeroportId: Long
    ): Collection<VolTrafficDTO>

    @Operation(
        summary = "Obtenir les vols par statut",
        description = "Retourne la liste des vols filtrés par statut"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Collectione des vols par statut récupérée avec succès"
        ),
        ApiResponse(
            responseCode = "404",
            description = "Aéroport non trouvé"
        )
    ])
    @GetMapping("/{aeroportId}/vols-par-statut/{statut}")
    fun getVolsParStatut(
        @Parameter(description = "ID de l'aéroport", required = true)
        @PathVariable aeroportId: Long,
        @Parameter(description = "Statut du vol", required = true)
        @PathVariable statut: Statut
    ): Collection<VolTrafficDTO>

    @Operation(
        summary = "Obtenir les vols en cours",
        description = "Retourne la liste des vols actuellement en vol"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Collectione des vols en cours récupérée avec succès"
        ),
        ApiResponse(
            responseCode = "404",
            description = "Aéroport non trouvé"
        )
    ])
    @GetMapping("/{aeroportId}/vols-en-cours")
    fun getVolsEnCours(
        @Parameter(description = "ID de l'aéroport", required = true)
        @PathVariable aeroportId: Long
    ): Collection<VolTrafficDTO>

    @Operation(
        summary = "Obtenir l'historique d'un vol",
        description = "Retourne l'historique des changements de statut d'un vol"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Historique récupéré avec succès"
        ),
        ApiResponse(
            responseCode = "404",
            description = "Historique non trouvé"
        )
    ])
    @GetMapping("/vol/{volId}/historique")
    fun getHistoriqueVol(
        @Parameter(description = "ID du vol", required = true)
        @PathVariable volId: Long
    ): HistoriqueResponseDTO?

    @Operation(
        summary = "Obtenir tous les historiques d'un aéroport",
        description = "Retourne les historiques de tous les vols de l'aéroport"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Historiques récupérés avec succès"
        )
    ])
    @GetMapping("/{aeroportId}/historiques")
    fun getHistoriquesAeroport(
        @Parameter(description = "ID de l'aéroport", required = true)
        @PathVariable aeroportId: Long
    ): Collection<HistoriqueResponseDTO>

    @Operation(
        summary = "Obtenir les plannings des pistes d'un aéroport",
        description = "Retourne tous les plannings de pistes pour cet aéroport"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Plannings récupérés avec succès"
        )
    ])
    @GetMapping("/{aeroportId}/plannings-pistes")
    fun getPlanningsPistesAeroport(
        @Parameter(description = "ID de l'aéroport", required = true)
        @PathVariable aeroportId: Long
    ): Collection<PlanningPistesResponseDTO>

    @Operation(
        summary = "Obtenir le planning d'une piste",
        description = "Retourne le planning d'une piste spécifique"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "Planning récupéré avec succès"
        )
    ])
    @GetMapping("/piste/{pisteId}/planning")
    fun getPlanningPiste(
        @Parameter(description = "ID de la piste", required = true)
        @PathVariable pisteId: Long
    ): Collection<PlanningPistesResponseDTO>

    @Operation(
        summary = "Obtenir les vols entrant de l'aeroport partenaire Soummam - Abane-Ramdane",
        description = "Retourne les vols entrant de l'aeroport partenaire Soummam"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Liste des vols récupérée avec succès"
            ),
            ApiResponse(
                responseCode = "502",
                description = "Erreur lors de l’appel à l'API externe BJA"
            ),
            ApiResponse(
                responseCode = "500",
                description = "Erreur interne du serveur"
            )
        ]
    )
    @GetMapping("/partenaires/BJA/departs")
    fun getVolsDepartBja(): Collection<VolTrafficDTO>


    @Operation(
        summary = "obtenir les vols sortants de l'aeroport partenaire Soummam - Abane-Ramdane",
        description = "Retourne les vols entrant de l'aeroport partenaire Soummam"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Liste des vols récupérée avec succès"
            ),
            ApiResponse(
                responseCode = "502",
                description = "Erreur lors de l’appel à l'API externe BJA"
            ),
            ApiResponse(
                responseCode = "500",
                description = "Erreur interne du serveur"
            )
        ]
    )
    @GetMapping("/partenaires/BJA/arrivees")
    fun getVolsArriveeBja(): Collection<VolTrafficDTO>

}