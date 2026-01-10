import {Statut} from "./Statut";

export interface VolTraffic {
  id: number | null;
  numeroVol: string;
  compagnie: string;
  origineId: number | null;
  origineNom: string | null;
  origineCodeIATA: string | null;
  destinationId: number | null;
  destinationNom: string | null;
  destinationCodeIATA: string | null;
  dateDepart: Date;
  dateArrivee: Date;
  statut: Statut;
  pisteDecollageId: number | null;
  pisteAtterissageId: number | null;
  avionId: number | null;
  retard?: number | null;
}

export interface StatistiquesTraffic {
  totalVolsDepart: number;
  totalVolsArrivee: number;
  volsEnCours: number;
  volsRetardes: number;
  volsAnnules: number;
  tauxOccupationPistes: number;
  tauxOccupationHangars: number;
}

export interface TableauDeBordTraffic {
  aeroportId: number;
  aeroportNom: string;
  aeroportCodeIATA: string;
  statistiques: StatistiquesTraffic;
  volsDepart: VolTraffic[];
  volsArrivee: VolTraffic[];
  pistesDisponibles: number;
  pistesOccupees: number;
  pistesEnMaintenance: number;
  hangarsDisponibles: number;
  hangarsOccupes: number;
  timestamp: Date;
}

