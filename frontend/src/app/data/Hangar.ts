import {EtatHangar} from './EtatHangar';

export interface Hangar{
  id: number;
  capacite: number;
  etat: EtatHangar;
  avionId?: number;
}
