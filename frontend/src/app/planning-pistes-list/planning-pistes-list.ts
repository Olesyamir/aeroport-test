import {Component, Injectable} from '@angular/core';
import {FormsModule} from "@angular/forms";
import {NgForOf, NgIf} from "@angular/common";
import {PlanningPistes} from "../data/PlanningPistes";
import {Vol} from "../data/Vol";
import {PlanningPistesServices} from "../services/planningPistesService/planning-pistes";
import {VolServivce} from "../services/volService/vol";



@Component({
  selector: 'app-planning-pistes-list',
  imports: [
    FormsModule, NgForOf, NgIf,
  ],
  templateUrl: './planning-pistes-list.html',
  styleUrl: './planning-pistes-list.css'
})

@Injectable({ providedIn: 'root' })
export class PlanningPistesList {
  planningPistes : PlanningPistes[] = [];
  vols : Vol[] = [];

  newPlanningPistes : PlanningPistes = {
    id : 0,
    startTime: new Date(),
    endTime: new Date(),
    priorite: 'NORMALE',
    remarques: '',
    volIds : [],
    usage: 'DECOLLAGE',
    volStatut : [],
    pisteId : []
  }

  editingId: number | null = null;
  originalPlanningPistes : PlanningPistes | null = null;

  constructor(
      private readonly planningPistesServices : PlanningPistesServices,
      private readonly volService : VolServivce
  ) {}

  ngOnInit(): void {
    this.chargerPlanningPistes();
    this.chargerVols();
  }

  chargerVols(): void {
    this.volService.getAllVols().subscribe({
      next: (data, ) => {
        this.vols = data??[];
        console.log(data);
      },
      error: (err) => {
        alert('Impossible de charger les pistes. Vérifiez que le backend est lancé.');
      }
    });
  }

  chargerPlanningPistes(): void {
    this.planningPistesServices.getAllPlanningPistes().subscribe({
      next: (data, ) => {
        this.planningPistes = data??[];
        console.log(data);
      },
      error: (err) => {
        alert('Impossible de charger les planning pistes. Vérifiez que le backend est lancé.');
      }
    });
  }

  supprimerPlanningPistes(id: number): void {
    if (!confirm('Voulez-vous vraiment supprimer cet planning des pistes ?')) {
      return;
    }

    this.planningPistesServices.deletePlanningPistes(id).subscribe({
      next: () => {
        this.planningPistes = this.planningPistes.filter(planningPistes => planningPistes.id !== id);
      },
      error: (err) => {
        alert('Impossible de supprimer le planning des pistes.');
      }
    });
  }

  ajouterPlanningPistes(): void {

    const createRequest = {
      startTime: this.newPlanningPistes.startTime,
      endTime: this.newPlanningPistes.endTime,
      priorite: this.newPlanningPistes.priorite,
      remarques: this.newPlanningPistes.remarques,
      volIds : this.newPlanningPistes.volIds,
      usage: this.newPlanningPistes.usage
    };

    this.planningPistesServices.createPlanningPistes(createRequest).subscribe({
      next: (planningPistesCree) => {
        this.planningPistes.push(planningPistesCree);
        this.newPlanningPistes = {
          id : this.newPlanningPistes.id,
          startTime: this.newPlanningPistes.startTime,
          endTime: this.newPlanningPistes.endTime,
          priorite: this.newPlanningPistes.priorite,
          remarques: this.newPlanningPistes.remarques,
          volIds : this.newPlanningPistes.volIds,
          usage: this.newPlanningPistes.usage,
          pisteId : this.newPlanningPistes.pisteId,
          volStatut : this.newPlanningPistes.volStatut
        };
      },
      error: (err) => {
        alert('Impossible de créer le .');
      }
    });
  }


  modifierPlanningPistes(id: number): void {
    this.editingId = id;

    const planningPistes = this.planningPistes.find(planningPistes => planningPistes.id === id);
    if (planningPistes) {
      this.originalPlanningPistes = { ...planningPistes };
    }
  }

  sauvegarderModification(): void {
    if (this.editingId === null) return;

    const planningPistes = this.planningPistes.find(pp => pp.id === this.editingId);
    if (!planningPistes) return;

    const updateRequest =  {
      startTime: planningPistes.startTime,
      endTime: planningPistes.endTime,
      priorite: planningPistes.priorite,
      remarques: planningPistes.remarques,
      volIds : planningPistes.volIds,
      pisteId : planningPistes.pisteId,
      usage: planningPistes.usage,
      volStatut : planningPistes.volStatut
    }


    this.planningPistesServices.updatePlanningPistes(this.editingId, updateRequest).subscribe({
      next: (planningPistesModifie) => {
        const index = this.planningPistes.findIndex(pp => pp.id === this.editingId);
        if (index !== -1) {
          this.planningPistes[index] = planningPistesModifie;
        }
        this.editingId = null;
        this.originalPlanningPistes = null;
      },
      error: (err) => {
        alert('Impossible de modifier le planning des pistes.');
      }
    });
  }


  annulerModification(): void {
    if (this.editingId !== null && this.originalPlanningPistes) {
      const index = this.planningPistes.findIndex(pp => pp.id === this.editingId);
      if (index !== -1) {
        this.planningPistes[index] = { ...this.originalPlanningPistes };
      }
    }

    this.editingId = null;
    this.originalPlanningPistes = null;
  }


 /* onToggleVol(list: number[] | undefined, id: number, checked:
  boolean) {
    list ??= [];
    if (checked) {
      if (!list.includes(id)) list.push(id);
    } else {
      const i = list.indexOf(id);
      if (i !== -1) list.splice(i, 1);
    }
  }*/

}
