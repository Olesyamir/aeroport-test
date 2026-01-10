import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import {Avion} from '../data/Avion';
import { FormsModule } from '@angular/forms';
import {Hangar} from '../data/Hangar';
import {Piste} from '../data/Piste';
import {AvionServivce} from "../services/avionService/avion";
import {NgForOf, NgIf} from "@angular/common";
import {Vol} from "../data/Vol";
import {PisteService} from "../services/pisteService/piste";
import {HangarService} from "../services/hangarService/hangar";
import {VolServivce} from "../services/volService/vol";
import { MatSelectModule } from '@angular/material/select';

@Component({
  selector: 'app-avion-list',
  imports: [
    FormsModule, NgForOf, NgIf,
    MatSelectModule
  ],
  templateUrl: './avion-list.html',
  styleUrl: './avion-list.css',
  encapsulation: ViewEncapsulation.Emulated,
  standalone: true,
})

export class AvionList implements OnInit {
  avions: Avion[] = [];
  hangars: Hangar[] = [];
  pistes: Piste[] = [];
  vols: Vol[] = [];
  searchNumImmatricule = '';
  avionRecherche: Avion | null = null;
  erreurRecherche: string | null = null;
  creationMessage: string | null = null;
  editionMessage: string | null = null;

  newAvion: Avion = {
    id : 0,
    nom: '',
    numImmatricule: '',
    type: '',
    capacite: 0,
    etat: 'AUSOL',
    hangarId: 0,
    volIds: []
  };

  editingId: number | null = null;
  originalAvion: Avion | null = null;

  constructor(
      private readonly avionService: AvionServivce,
      private readonly pisteService : PisteService,
      private readonly hangarService : HangarService,
      private readonly volService : VolServivce
      ) {}

  ngOnInit(): void {
    this.chargerAvions();
    this.chargerPistes();
    this.chargerHangars();
    this.chargerVols();
  }

  chargerAvions(): void {
    this.avionService.getAllAvions().subscribe({
      next: (data, ) => {
        this.avions = data??[];
        console.log(data);
      },
      error: (err) => {
        alert('Impossible de charger les avions. V€÷rifiez que le backend est lanc€÷.');
      }
    });
  }

  rechercherAvionParImmatriculation(): void {
    if (!this.searchNumImmatricule) {
      this.avionRecherche = null;
      this.erreurRecherche = null;
      return;
    }

    this.avionService.searchByNumImmatricule(this.searchNumImmatricule).subscribe({
      next: (avion) => {
        this.avionRecherche = avion;
        this.erreurRecherche = null;
      },
      error: (err) => {
        this.avionRecherche = null;
        const backendMessage =
          err?.error?.error ??
          err?.error?.message ??
          err?.error?.detail ??
          err?.error?.title;
        this.erreurRecherche =
          backendMessage ||
          'Aucun avion trouv€÷ pour cette immatriculation ou erreur lors de la recherche.';
      }
    });
  }

  chargerPistes(): void {
    this.pisteService.getAllPistes().subscribe({
      next: (data, ) => {
        this.pistes = data??[];
        console.log(data);
      },
      error: (err) => {
        alert('Impossible de charger les pistes. V€÷rifiez que le backend est lanc€÷.');
      }
    });
  }

  chargerHangars(): void {
    this.hangarService.getAllHangars().subscribe({
      next: (data, ) => {
        this.hangars = data??[];
        console.log(data);
      },
      error: (err) => {
        alert('Impossible de charger les hangars. V€÷rifiez que le backend est lanc€÷.');
      }
    });
  }

  chargerVols(): void {
    this.volService.getAllVols().subscribe({
      next: (data, ) => {
        this.vols = data??[];
        console.log(data);
      },
      error: (err) => {
        alert('Impossible de charger les vols. V€÷rifiez que le backend est lanc€÷.');
      }
    });
  }

  supprimerAvion(id: number): void {
    if (!confirm('Voulez-vous vraiment supprimer cet avion ?')) {
      return;
    }

    this.avionService.deleteAvion(id).subscribe({
      next: () => {
        this.avions = this.avions.filter(avion => avion.id !== id);
      },
      error: (err) => {
        alert('Impossible de supprimer l\'avion.');
      }
    });
  }

  ajouterAvion(): void {
    this.creationMessage = null;

    const champsManquants: string[] = [];
    if (!this.newAvion.nom) {
      champsManquants.push('nom');
    }
    if (!this.newAvion.numImmatricule) {
      champsManquants.push('immatriculation');
    }
    if (!this.newAvion.type) {
      champsManquants.push('type');
    }
    if (!this.newAvion.capacite || this.newAvion.capacite <= 0) {
      champsManquants.push('capacit€÷');
    }
    if (!this.newAvion.etat) {
      champsManquants.push('€÷tat');
    }

    if (champsManquants.length > 0) {
      this.creationMessage = 'Champs manquants : ' + champsManquants.join(', ') + '.';
      return;
    }

    const createRequest = {
      nom: this.newAvion.nom,
      numImmatricule: this.newAvion.numImmatricule,
      type: this.newAvion.type,
      capacite: this.newAvion.capacite,
      etat: this.newAvion. etat,
      hangarId: this.newAvion.hangarId,
      volIds: this.newAvion.volIds ?? []

    };

    this.avionService.createAvion(createRequest).subscribe({
      next: (avionCree) => {
        this.avions.push(avionCree);
        this.creationMessage = null;
        this.newAvion = {
          id : this.newAvion.id,
          nom: this.newAvion.nom,
          numImmatricule: this.newAvion.numImmatricule,
          type: this.newAvion.type,
          capacite: this.newAvion.capacite,
          etat : this.newAvion. etat,
          hangarId : this.newAvion.hangarId,
          volIds: this.newAvion.volIds
        };
      },
      error: (err) => {
        const backendMessage =
          err?.error?.error ??
          err?.error?.message ??
          err?.error?.detail ??
          err?.error?.title;
        this.creationMessage =
          backendMessage ||
          'Impossible de cr€÷er l\'avion.';
      }
    });
  }

  modifierAvion(id: number): void {
    this.editingId = id;
    this.editionMessage = null;

    const avion = this.avions.find(avion => avion.id === id);
    if (avion) {
      this.originalAvion = { ...avion };
    }
  }

  sauvegarderModification(): void {
    this.editionMessage = null;
    if (this.editingId === null) return;

    const avion = this.avions.find(a => a.id === this.editingId);
    if (!avion) return;

    const updateRequest = {
      nom: avion.nom,
      numImmatricule: avion.numImmatricule,
      type: avion.type,
      capacite: avion.capacite,
      etat: avion. etat,
      hangarId: avion?.hangarId,
      volsId:avion.volIds ?? []
    };

    this.avionService.updateAvion(this.editingId, updateRequest).subscribe({
      next: (avionModifie) => {
        const index = this.avions.findIndex(a => a.id === this.editingId);
        if (index !== -1) {
          this.avions[index] = avionModifie;
        }
        this.editingId = null;
        this.originalAvion = null;
      },
      error: (err) => {
        const backendMessage =
          err?.error?.error ??
          err?.error?.message ??
          err?.error?.detail ??
          err?.error?.title;
        this.editionMessage =
          backendMessage ||
          'Impossible de modifier l\'avion.';
      }
    });
  }


  annulerModification(): void {
    if (this.editingId !== null && this.originalAvion) {
      const index = this.avions.findIndex(a => a.id === this.editingId);
      if (index !== -1) {
        this.avions[index] = { ...this.originalAvion };
      }
    }

    this.editingId = null;
    this.originalAvion = null;
    this.editionMessage = null;
  }


  onToggleVol(list: number[] | undefined, id: number, checked:
  boolean) {
    list ??= [];
    if (checked) {
      if (!list.includes(id)) list.push(id);
    } else {
      const i = list.indexOf(id);
      if (i !== -1) list.splice(i, 1);
    }
  }

}
