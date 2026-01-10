import {EtatMateriel} from './EtatMateriel';


export interface Avion {
  id: number;
  nom: string;
  numImmatricule: string;
  type: string;
  capacite: number;
  etat: EtatMateriel;
  hangarId?: number;
  volIds?: number[] ;

}
