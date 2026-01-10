import { Routes } from '@angular/router';
import { AvionList } from './avion-list/avion-list';
import { HangarList } from './hangar-list/hangar-list';
import { PisteList } from './piste-list/piste-list';
import {Accueil} from './accueil/accueil';
import {VolList} from "./vol-list/vol-list";
import {HistoriqueList} from "./historique-list/historique-list";
import {PlanningPistesList} from "./planning-pistes-list/planning-pistes-list";
import {AeroportList} from "./aeroport-list/aeroport-list";
import {TableauDeBordTraficList} from "./tableauDeBordTrafic-list/tableauDeBordTrafic-list";

export const routes: Routes = [
  {path: '', component: Accueil},
  { path: 'avions', component: AvionList },
  { path: 'hangars', component: HangarList },
  { path: 'pistes', component: PisteList },
  { path : 'vols' , component: VolList},
  { path : 'historiques' , component: HistoriqueList},
  { path : 'planningPistes' , component: PlanningPistesList},
  { path : 'aeroports' , component: AeroportList},
  { path : 'tableau-de-bord-trafic' , component: TableauDeBordTraficList},
  { path: '**', redirectTo: '' }
];
