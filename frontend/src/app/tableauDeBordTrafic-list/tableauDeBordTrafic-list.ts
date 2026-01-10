import {Component, OnInit, ViewEncapsulation} from "@angular/core";
import {FormsModule} from "@angular/forms";
import {NgForOf, NgIf} from "@angular/common";
import {Aeroport} from "../data/Aeroport";
import {TableauDeBordTraffic} from "../data/TableauDeBordTrafic";
import {AeroportService} from "../services/aeroportService/aeroport";
import {TableauDeBordTrafficService} from "../services/tableauDeBordTrafficService/tableau-de-bord-traffic";

@Component({
  selector: 'app-tableau-de-bord-trafic',
  imports: [
    FormsModule,
    NgForOf,
    NgIf
  ],
  templateUrl: './tableauDeBordTrafic-list.html',
  styleUrl: './tableauDeBordTrafic-list.css',
  encapsulation: ViewEncapsulation.Emulated,
  standalone: true
})
export class TableauDeBordTraficList implements OnInit {

  aeroports: Aeroport[] = [];
  selectedAeroportId: number | null = null;
  tableau: TableauDeBordTraffic | null = null;
  chargement = false;
  erreur: string | null = null;
  filtreStatut: string = '';
  volsParStatut: TableauDeBordTraffic['volsDepart'] = [];
  volsEnCours: TableauDeBordTraffic['volsDepart'] = [];
  volsBjaDepart: TableauDeBordTraffic['volsDepart'] = [];
  volsBjaArrivee: TableauDeBordTraffic['volsDepart'] = [];
  erreurFiltre: string | null = null;
  erreurPartenaires: string | null = null;

  constructor(
    private readonly aeroportService: AeroportService,
    private readonly tableauService: TableauDeBordTrafficService
  ) {}

  ngOnInit(): void {
    this.chargerAeroports();
  }

  chargerAeroports(): void {
    this.aeroportService.getAllAeroports().subscribe({
      next: (data) => {
        this.aeroports = data ?? [];
        if (this.aeroports.length > 0) {
          this.selectedAeroportId = this.aeroports[0].id;
          this.chargerTableau();
        } else {
          this.erreur = 'Aucun aéroport trouvé. Créez un aéroport dans la section Aéroports.';
        }
      },
      error: (err) => {
        console.error('Erreur chargement aéroports', err);
        this.erreur = 'Impossible de charger les aéroports (voir console navigateur pour le détail).';
      }
    });
  }

  changerAeroport(): void {
    this.chargerTableau();
    this.volsParStatut = [];
    this.volsEnCours = [];
    this.erreurFiltre = null;
    this.volsBjaDepart = [];
    this.volsBjaArrivee = [];
    this.erreurPartenaires = null;
  }

  chargerTableau(): void {
    if (this.selectedAeroportId == null) {
      return;
    }
    this.chargement = true;
    this.erreur = null;

    this.tableauService.getTableauDeBord(this.selectedAeroportId).subscribe({
      next: (data) => {
        this.tableau = data;
        this.chargement = false;
      },
      error: (err) => {
        console.error('Erreur chargement tableau de bord', err);
        this.erreur = 'Impossible de charger le tableau de bord pour cet aéroport (voir console navigateur pour le détail).';
        this.chargement = false;
        this.tableau = null;
      }
    });
  }

  chargerVolsParStatut(): void {
    if (this.selectedAeroportId == null || !this.filtreStatut) {
      return;
    }

    this.tableauService.getVolsParStatut(this.selectedAeroportId, this.filtreStatut as any).subscribe({
      next: (data) => {
        this.volsParStatut = data ?? [];
        this.erreurFiltre = null;
      },
      error: (err) => {
        console.error('Erreur chargement vols par statut', err);
        this.erreurFiltre = 'Impossible de charger les vols par statut.';
        this.volsParStatut = [];
      }
    });
  }

  chargerVolsEnCours(): void {
    if (this.selectedAeroportId == null) {
      return;
    }

    this.tableauService.getVolsEnCours(this.selectedAeroportId).subscribe({
      next: (data) => {
        this.volsEnCours = data ?? [];
        this.erreurFiltre = null;
      },
      error: (err) => {
        console.error('Erreur chargement vols en cours', err);
        this.erreurFiltre = 'Impossible de charger les vols en cours.';
        this.volsEnCours = [];
      }
    });
  }

  chargerVolsBjaDepart(): void {
    this.tableauService.getVolsDepartBja().subscribe({
      next: (data) => {
        this.volsBjaDepart = data ?? [];
        this.erreurPartenaires = null;
      },
      error: (err) => {
        console.error('Erreur chargement vols partenaires BJA (départs)', err);
        this.erreurPartenaires = 'Impossible de charger les vols partenaires BJA (départs).';
        this.volsBjaDepart = [];
      }
    });
  }

  chargerVolsBjaArrivee(): void {
    this.tableauService.getVolsArriveeBja().subscribe({
      next: (data) => {
        this.volsBjaArrivee = data ?? [];
        this.erreurPartenaires = null;
      },
      error: (err) => {
        console.error('Erreur chargement vols partenaires BJA (arrivées)', err);
        this.erreurPartenaires = 'Impossible de charger les vols partenaires BJA (arrivées).';
        this.volsBjaArrivee = [];
      }
    });
  }
}
