package fr.uga.miage.m1.utils

import fr.uga.miage.m1.Application.requestDTO.*
import fr.uga.miage.m1.Domain.enums.*
import fr.uga.miage.m1.request.AeroportRequestDTO
import java.time.LocalDateTime
import java.time.LocalTime


fun avionRequest(
    numImmatricule: String,
    nom: String = "Airbus A320",
    type: String = "Passager",
    capacite: Int = 180,
    etat: EtatMateriel = EtatMateriel.AUSOL,
    hangarId: Long? = null,
    pisteId: Long? = null,
    volIds: Collection<Long>? = null
): AvionRequestDTO {
    return AvionRequestDTO(
        nom = nom,
        numImmatricule = numImmatricule,
        type = type,
        capacite = capacite,
        etat = etat,
        hangarId = hangarId,
        pisteId = pisteId,
        volIds = volIds
    )
}

fun hangarRequest(
    capacite: Int = 150,
    etat: Etat = Etat.LIBRE,
    avionIds: List<Long> = emptyList()
): HangarRequestDTO {
    return HangarRequestDTO(
        capacite = capacite,
        etat = etat,
        avionIds = avionIds
    )
}

//fun pisteRequest(
//    longueur: Double = 3000.0,
//    etat: Etat = Etat.LIBRE,
//    aeroportId: Long,
//    volsDepartIds: List<Long> = emptyList(),
//    volsArriveeIds: List<Long> = emptyList()
//): PisteRequestDTO {
//    return PisteRequestDTO(
//        longueur = longueur,
//        etat = etat,
//        volsDepartIds = volsDepartIds,
//        volsArriveeIds = volsArriveeIds
//    )
//}
fun pisteRequest(
    longueur: Double = 3000.0,
    etat: Etat = Etat.LIBRE,
    volsDepartIds: Collection<Long>? = null,
    volsArriveeIds: Collection<Long>? = null
): PisteRequestDTO {
    return PisteRequestDTO(
        longueur = longueur,
        etat = etat,
        volsDepartIds = volsDepartIds,
        volsArriveeIds = volsArriveeIds
    )
}

fun aeroportRequest(
    nom: String = "Test Airport",
    codeIATA: String = "TST",
    ville: String = "Test City",
    pays: String = "Test Country"
): AeroportRequestDTO {
    return AeroportRequestDTO(
        nom = nom,
        codeIATA = codeIATA,
        ville = ville,
        pays = pays
    )
}


fun volRequest(
    numeroVol: String,
    compagnie: String = "Air France",
    origineId: Long,
    destinationId: Long,
    dateDepart: LocalDateTime = LocalDateTime.now(),
    dateArrivee: LocalDateTime = LocalDateTime.now().plusHours(2),
    statut: Statut = Statut.ENATTENTE,
    typeVol: TypeVol = TypeVol.NORMAL,
    avionId: Long? = null,
    pisteDecollageId: Long? = null,
    pisteAtterissageId: Long? = null

): VolRequestDTO {
    return VolRequestDTO(
        numeroVol = numeroVol,
        compagnie = compagnie,
        origineId = origineId,
        destinationId = destinationId,
        dateDepart = dateDepart,
        dateArrivee = dateArrivee,
        statut = statut,
        typeVol = typeVol,
        avionId = avionId,
        pisteDecollageId = pisteDecollageId,
        pisteAtterissageId = pisteAtterissageId
    )
}

fun planningPisteRequest(
    startTime: LocalTime = LocalTime.of(8, 0),
    endTime: LocalTime = LocalTime.of(10, 0),
    priorite: Priorite = Priorite.NORMALE,
    remarques: String? = null,
    volIds: List<Long> = emptyList(),
    usage: Usage = Usage.DECOLLAGE
): PlanningPistesRequestDTO {
    return PlanningPistesRequestDTO(
        startTime = startTime,
        endTime = endTime,
        priorite = priorite,
        remarques = remarques,
        volIds = volIds,
        usage = usage
    )
}


fun historiqueRequest(
    idVol: Long,
    statut: Statut = Statut.ENATTENTE
): HistoriqueRequestDTO {
    return HistoriqueRequestDTO(
        idVol = idVol,
        statut = statut
    )
}
