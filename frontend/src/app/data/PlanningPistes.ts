import {Priorite} from "./Priorite";
import {Usage} from "./Usage";
import {Statut} from "./Statut";

export interface PlanningPistes {
    id : number
    startTime: Date,
    endTime: Date,
    priorite: Priorite,
    remarques: string,
    volIds :number[],
    usage: Usage,
    volStatut?:  Statut[] | null,
    pisteId : number[]
}