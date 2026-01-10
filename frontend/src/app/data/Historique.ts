import {Statut} from "./Statut";


export interface Historique {
    id : number,
    statut : Statut
    datetime : Date,
    idVol : number
}