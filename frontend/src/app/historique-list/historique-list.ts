import { Component } from '@angular/core';
import {Historique} from "../data/Historique";
import {HistoriqueService} from "../services/historiqueService/historique";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {NgForOf, NgIf} from "@angular/common";

@Component({
  selector: 'app-historique-list',
  imports: [
    FormsModule,
    NgForOf,
    NgIf,
    ReactiveFormsModule
  ],
  templateUrl: './historique-list.html',
  styleUrl: './historique-list.css'
})
export class HistoriqueList {

  historiques: Historique[] = [];

  newHistorique : Historique = {
    id : 0,
    statut : 'ENATTENTE' ,
    datetime :  new Date(),
    idVol : 0
  };

  editingId : number | null = null;
  originalHistorique : Historique |null = null;

  constructor( private historiqueService : HistoriqueService) {}

    ngOnInit(): void {
      this.chargerHistoriques()
  }

  chargerHistoriques() : void {
    this.historiqueService.getAllHistoriques().subscribe({
      next: (data) => {
        this.historiques = data;
        console.log(data);
      },
      error: (err) => {
        alert('Impossible de charger les hangars. Vérifiez que le backend est lancé.');
      }
    });
  }


   ajouterHistorique(): void {

    const createRequest = {
      id: this.newHistorique.id,
      idVol: this.newHistorique.idVol,
      datetime: this.newHistorique.datetime,
      statut: this.newHistorique.statut
    };

    this.historiqueService.createHistoriques(createRequest).subscribe({
      next: (historiqueCree) => {
        this.historiques.push(historiqueCree);
        this.newHistorique = {
          id: this.newHistorique.id,
          idVol: this.newHistorique.idVol,
          datetime: this.newHistorique.datetime,
          statut: this.newHistorique.statut };
      },
      error: (err) => {
        alert('Impossible de créer le hangar.');
      }
    });
  }

  supprimerHistorique(id: number): void {

    this.historiqueService.deleteHistoriqueById(id).subscribe({
      next: () => {
        this.historiques = this.historiques.filter(historique => historique.id !== id);
      },
      error: (err) => {
        alert('Impossible de supprimer l\'historique.');
      }
    });


  }


}
