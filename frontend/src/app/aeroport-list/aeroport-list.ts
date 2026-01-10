import {Component, OnInit, ViewEncapsulation} from "@angular/core";
import {FormsModule} from "@angular/forms";
import {NgForOf, NgIf} from "@angular/common";
import {Aeroport} from "../data/Aeroport";
import {AeroportService} from "../services/aeroportService/aeroport";

@Component({
  selector: 'app-aeroport-list',
  imports: [
    FormsModule,
    NgForOf,
    NgIf
  ],
  templateUrl: './aeroport-list.html',
  styleUrl: './aeroport-list.css',
  encapsulation: ViewEncapsulation.Emulated,
  standalone: true
})
export class AeroportList implements OnInit {

  aeroports: Aeroport[] = [];
  searchCodeIATA = '';
  aeroportRecherche: Aeroport | null = null;
  erreurRecherche: string | null = null;
  creationMessage: string | null = null;
  editionMessage: string | null = null;

  newAeroport: Aeroport = {
    id: 0,
    nom: '',
    ville: '',
    pays: '',
    codeIATA: '',
    volsDepartIds: [],
    volsArriveeIds: []
  };

  editingId: number | null = null;
  originalAeroport: Aeroport | null = null;

  constructor(private readonly aeroportService: AeroportService) {}

  ngOnInit(): void {
    this.chargerAeroports();
  }

  chargerAeroports(): void {
    this.aeroportService.getAllAeroports().subscribe({
      next: (data) => {
        this.aeroports = data ?? [];
        console.log(data);
      },
      error: () => {
        alert('Impossible de charger les aéroports. Vérifiez que le backend est lancé.');
      }
    });
  }

  rechercherAeroportParCode(): void {
    if (!this.searchCodeIATA) {
      this.aeroportRecherche = null;
      this.erreurRecherche = null;
      return;
    }

    this.aeroportService.searchByCodeIATA(this.searchCodeIATA).subscribe({
      next: (aeroport) => {
        this.aeroportRecherche = aeroport;
        this.erreurRecherche = null;
      },
      error: (err) => {
        this.aeroportRecherche = null;
        const backendMessage =
          err?.error?.error ??
          err?.error?.message ??
          err?.error ??
          err?.message;
        this.erreurRecherche =
          backendMessage ||
          'Aucun aéroport trouvé pour ce code IATA ou erreur lors de la recherche.';
      }
    });
  }

  ajouterAeroport(): void {
    this.creationMessage = null;

    const champsManquants: string[] = [];
    if (!this.newAeroport.nom) {
      champsManquants.push('nom');
    }
    if (!this.newAeroport.codeIATA) {
      champsManquants.push('code IATA');
    }

    if (champsManquants.length > 0) {
      this.creationMessage = 'Champs manquants : ' + champsManquants.join(', ') + '.';
      return;
    }

    const dto = {
      nom: this.newAeroport.nom,
      ville: this.newAeroport.ville,
      pays: this.newAeroport.pays,
      codeIATA: this.newAeroport.codeIATA
    };

    this.aeroportService.createAeroport(dto).subscribe({
      next: (aeroportCree) => {
        this.aeroports.push(aeroportCree);
        this.creationMessage = null;
        this.newAeroport = {
          id: 0,
          nom: '',
          ville: '',
          pays: '',
          codeIATA: '',
          volsDepartIds: [],
          volsArriveeIds: []
        };
      },
      error: (err) => {
        const backendMessage =
          err?.error?.error ??
          err?.error?.message ??
          err?.error ??
          err?.message;
        this.creationMessage =
          backendMessage ||
          'Impossible de créer l\'aéroport.';
      }
    });
  }

  modifierAeroport(id: number): void {
    this.editingId = id;
    this.editionMessage = null;
    const aeroport = this.aeroports.find(a => a.id === id);
    if (aeroport) {
      this.originalAeroport = {...aeroport};
    }
  }

  sauvegarderModification(): void {
    this.editionMessage = null;
    if (this.editingId === null) {
      return;
    }

    const index = this.aeroports.findIndex(a => a.id === this.editingId);
    if (index === -1) {
      return;
    }

    const courant = this.aeroports[index];

    const dto = {
      nom: courant.nom,
      ville: courant.ville,
      pays: courant.pays,
      codeIATA: courant.codeIATA
    };

    this.aeroportService.updateAeroport(this.editingId, dto).subscribe({
      next: (aeroportModifie) => {
        this.aeroports[index] = aeroportModifie;
        this.editingId = null;
        this.originalAeroport = null;
      },
      error: (err) => {
        const backendMessage =
          err?.error?.error ??
          err?.error?.message ??
          err?.error ??
          err?.message;
        this.editionMessage =
          backendMessage ||
          'Impossible de modifier l\'aéroport.';
      }
    });
  }

  annulerModification(): void {
    if (this.editingId !== null && this.originalAeroport) {
      const index = this.aeroports.findIndex(a => a.id === this.editingId);
      if (index !== -1) {
        this.aeroports[index] = {...this.originalAeroport};
      }
    }
    this.editingId = null;
    this.originalAeroport = null;
    this.editionMessage = null;
  }

  supprimerAeroport(id: number): void {
    if (!confirm('Supprimer cet aéroport ?')) {
      return;
    }

    this.aeroportService.deleteAeroport(id).subscribe({
      next: () => {
        this.aeroports = this.aeroports.filter(a => a.id !== id);
      },
      error: (err) => {
        const backendMessage =
          err?.error?.error ??
          err?.error?.message ??
          err?.error ??
          err?.message;
        alert(backendMessage || 'Impossible de supprimer l\'aéroport.');
      }
    });
  }
}
