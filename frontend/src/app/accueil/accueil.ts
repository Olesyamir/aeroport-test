import {Component, ViewEncapsulation} from '@angular/core';
import {TableauDeBordTraficList} from "../tableauDeBordTrafic-list/tableauDeBordTrafic-list";

@Component({
  selector: 'app-accueil',
  standalone: true,
  imports: [
    TableauDeBordTraficList
  ],
  templateUrl: './accueil.html',
  styleUrl: './accueil.css',
  encapsulation: ViewEncapsulation.Emulated
})
export class Accueil {}

