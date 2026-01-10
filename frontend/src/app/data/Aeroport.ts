export interface Aeroport {
  id: number;
  nom: string;
  ville: string;
  pays: string;
  codeIATA: string;
  volsDepartIds: number[];
  volsArriveeIds: number[];
}
