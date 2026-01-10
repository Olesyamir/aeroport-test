import {Statut} from "./Statut";
import {TypeVol} from "./TypeVol";

export interface Vol {
    id: number,
    numeroVol: String,
    compagnie: String,
    origineId?: number,
    destinationId?: number,
    dateDepart: Date,
    dateArrivee: Date,
    statut: Statut,
    typeVol: TypeVol,
    avionId?: number ,
    pisteDecollageId? : number,
    pisteAtterissageId? : number,

}
