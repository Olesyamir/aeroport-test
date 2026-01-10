import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { NgForOf, NgIf} from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Piste} from '../data/Piste';
import { PisteService } from '../services/pisteService/piste';

@Component({
  selector: 'app-piste-list',
  imports: [NgForOf, NgIf, FormsModule],
  templateUrl: './piste-list.html',
  styleUrl: './piste-list.css',
  standalone: true,
  encapsulation: ViewEncapsulation.Emulated
})
export class PisteList implements OnInit {
  pistes: Piste[] = [];

  newPiste: Piste = {
    id : 0,
    longueur: 0,
    etat: 'LIBRE'
  };

  editingId: number | null = null;
  private pisteBackup: Piste | null = null;

  constructor(
    private pisteService: PisteService
  ) {}

  ngOnInit(): void {
    this.chargerPistes();
  }

  chargerPistes(): void {
    this.pisteService.getAllPistes().subscribe({
      next: (data) => {
        this.pistes = data;
      },
      error: (err) => {
        alert('Impossible de charger les pistes. Vérifiez que le backend est lancé.');
        console.error('Erreur chargement pistes', err);
      }
    });
  }

  supprimerPiste(id: number): void {
    if (!confirm('Voulez-vous vraiment supprimer cette piste ?')) {
      return;
    }

    this.pisteService.deletePiste(id).subscribe({
      next: () => {
        this.pistes = this.pistes.filter(piste => piste.id !== id);
      },
      error: (err) => {
        alert('Impossible de supprimer la piste.');
        console.error('Erreur suppression piste', err);
      }
    });
  }

  modifierPiste(id: number): void {
    const piste = this.pistes.find(p => p.id === id);
    if (piste) {
      this.pisteBackup = { ...piste };
      this.editingId = id;
    }
  }

  sauvegarderModification(): void {
    if (this.editingId === null) return;

    const piste = this.pistes.find(p => p.id === this.editingId);
    if (!piste) return;

    const updateRequest = {
      longueur: piste.longueur,
      etat: piste.etat
    };

    this.pisteService.updatePiste(this.editingId, updateRequest).subscribe({
      next: (pisteModifiee) => {
        const index = this.pistes.findIndex(p => p.id === this.editingId);
        if (index !== -1) {
          this.pistes[index] = pisteModifiee;
        }
        this.editingId = null;
        this.pisteBackup = null;
      },
      error: (err) => {
        alert('Impossible de modifier la piste.');
        console.error('Erreur modification piste', err);
      }
    });
  }

  annulerModification(): void {
    if (this.pisteBackup && this.editingId !== null) {
      const index = this.pistes.findIndex(p => p.id === this.editingId);
      if (index !== -1) {
        this.pistes[index] = { ...this.pisteBackup };
      }
    }
    this.editingId = null;
    this.pisteBackup = null;
  }

  ajouterPiste(): void {
    if (!this.newPiste.longueur) {
      alert('Veuillez remplir tous les champs obligatoires.');
      return;
    }

    const createRequest = {
      id : 0,
      longueur: this.newPiste.longueur,
      etat: this.newPiste.etat
    };

    this.pisteService.createPiste(createRequest).subscribe({
      next: (pisteCree) => {
        this.pistes.push(pisteCree);
        this.newPiste = {
          id : 0,
          longueur: 0,
          etat: 'LIBRE'
        };
      },
      error: (err) => {
        alert('Impossible de créer la piste.');
        console.error('Erreur création piste', err);
      }
    });
  }

}

