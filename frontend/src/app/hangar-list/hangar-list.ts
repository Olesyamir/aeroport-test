import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import {Hangar} from '../data/Hangar';
import {FormsModule} from '@angular/forms';
import {NgForOf, NgIf} from '@angular/common';
import { HangarService } from '../services/hangarService/hangar';

@Component({
  selector: 'app-hangar-list',
  imports: [
    FormsModule,
    NgForOf,
    NgIf
  ],
  templateUrl: './hangar-list.html',
  styleUrl: './hangar-list.css',
  standalone: true,
})
export class HangarList implements OnInit {
  hangars: Hangar[] = [];

  newHangar: Hangar = { id: 0, capacite: 0, etat: 'LIBRE' };
  editingId: number | null = null;
  originalHangar: Hangar | null = null;

  constructor(private hangarService: HangarService) {}

  ngOnInit(): void {
    this.chargerHangars();
  }

  chargerHangars(): void {
    this.hangarService.getAllHangars().subscribe({
      next: (data) => {
        this.hangars = data;
        console.log(data);
      },
      error: (err) => {
        alert('Impossible de charger les hangars. Vérifiez que le backend est lancé.');
      }
    });
  }

  ajouterHangar(): void {
    if (!this.newHangar.capacite) {
      alert('Veuillez remplir tous les champs obligatoires.');
      return;
    }

    const createRequest = {
      id: 0,
      capacite: this.newHangar.capacite,
      etat: this.newHangar.etat,
      avionId: null
    };

    this.hangarService.createHangar(createRequest).subscribe({
      next: (hangarCree) => {
        this.hangars.push(hangarCree);
        this.newHangar = { id: this.newHangar.id, capacite: this.newHangar.capacite, etat: this.newHangar.etat };
      },
      error: (err) => {
        alert('Impossible de créer le hangar.');
      }
    });
  }

  supprimerHangar(id: number): void {
    if (!confirm('Voulez-vous vraiment supprimer ce hangar ?')) {
      return;
    }

    this.hangarService.deleteHangar(id).subscribe({
      next: () => {
        this.hangars = this.hangars.filter(h => h.id !== id);
      },
      error: (err) => {
        alert('Impossible de supprimer le hangar.');
      }
    });
  }


  modifierHangar(id: number): void {
    this.editingId = id;
    const index = this.hangars.findIndex(h => h.id === id);
    if (index !== -1) {
      this.originalHangar = { ...this.hangars[index] };
    }
  }

  sauvegarderModification(): void {
    if (this.editingId === null) return;

    const index = this.hangars.findIndex(h => h.id === this.editingId);

    if (index === -1) return;

    const courant = this.hangars[index];

    const updateRequest = {
      capacite: Number(courant.capacite),
      etat: courant.etat
    };

    this.hangarService.updateHangar(this.editingId, updateRequest).subscribe({
      next: (hangarModifie) => {
        this.hangars[index] = hangarModifie ? hangarModifie : { ...courant, ...updateRequest };
        this.editingId = null;
        this.originalHangar = null;
      },
      error: () => {
        if (this.originalHangar) this.hangars[index] = { ...this.originalHangar };
        alert('Impossible de modifier le hangar.');
      }
    });
  }


  annulerModification(): void {
    if (this.editingId !== null && this.originalHangar) {
      const index = this.hangars.findIndex(h => h.id === this.editingId);
      if (index !== -1) this.hangars[index] = { ...this.originalHangar };
    }
    this.editingId = null;
    this.originalHangar = null;
  }

}
