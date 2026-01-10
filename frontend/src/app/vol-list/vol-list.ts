import {Component, OnInit, ViewEncapsulation} from "@angular/core";
import {FormsModule} from "@angular/forms";
import {NgForOf, NgIf} from "@angular/common";
import {CreateVolRequest, UpdateVolRequest, VolServivce} from "../services/volService/vol";
import {Vol} from "../data/Vol";
import {Avion} from "../data/Avion";
import {AvionServivce} from "../services/avionService/avion";
import {Piste} from "../data/Piste";
import {PisteService} from "../services/pisteService/piste";
import {Aeroport} from "../data/Aeroport";
import {AeroportService} from "../services/aeroportService/aeroport";

@Component({
    selector: 'app-vol-list',
    imports: [
        FormsModule, NgForOf, NgIf,
    ],
    templateUrl: './vol-list.html',
    styleUrl: './vol-list.css',
    encapsulation: ViewEncapsulation.Emulated,
    standalone: true,
})
export class VolList implements OnInit  {
    vols: Vol[] = [];
    avions: Avion[] = [];
    aeroports: Aeroport[] = [];
    pistesDecollages: Piste[] = [];
    pistesAtterissages: Piste[] = [];
    searchVolId: number | null = null;
    statutVolRecherche: string | null = null;
    erreurRechercheStatut: string | null = null;
    anwaysAeroport: Aeroport | null = null;

    newVol: Vol = {
        id: 0,
        numeroVol: '',
        compagnie: '',
        origineId: undefined,
        destinationId: undefined,
        dateDepart: new Date(),
        dateArrivee: new Date(),
        statut: 'ENATTENTE',
        typeVol: 'SURCLASSE',
        avionId:  undefined,
        pisteDecollageId:  undefined,
        pisteAtterissageId:  undefined,
    };

    editingId: number | null = null;
    originalVol: Vol | null = null;

    constructor(
        private volService: VolServivce,
        private avionService : AvionServivce,
        private pisteService : PisteService,
        private aeroportService: AeroportService
    ) {}

    private toDatetimeLocalInput(value: unknown): string {
        if (value == null) {
            return '';
        }

        const date = value instanceof Date ? value : new Date(value as string);
        if (Number.isNaN(date.getTime())) {
            return String(value);
        }

        const pad = (n: number) => String(n).padStart(2, '0');
        return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}T${pad(date.getHours())}:${pad(date.getMinutes())}`;
    }

    ngOnInit(): void {
        this.chargerVols();
        this.chargerPistes();
        this.chargerAvions();
        this.chargerAeroports();
    }

    supprimerVol(id: number): void {
        this.volService.deleteVol(id).subscribe({
            next: () => {
                this.vols = this.vols.filter(vol => vol.id !== id);
            },
            error: () => {
                alert('Impossible de supprimer le vol.');
            }
        });
    }

    rechercherStatutVol(): void {
        if (this.searchVolId == null) {
            this.statutVolRecherche = null;
            this.erreurRechercheStatut = null;
            return;
        }

        this.volService.findStatutById(this.searchVolId).subscribe({
            next: (statut) => {
                this.statutVolRecherche = statut;
                this.erreurRechercheStatut = null;
            },
            error: () => {
                this.statutVolRecherche = null;
                this.erreurRechercheStatut = 'Vol introuvable ou erreur lors de la recherche du statut.';
            }
        });
    }

    chargerAvions(): void {
        this.avionService.getAllAvions().subscribe({
            next: (data) => {
                this.avions = data??[];
                console.log(data);
            },
            error: () => {
                alert('Impossible de charger les avions. Vérifiez que le backend est lancé.');
            }
        });
    }

    chargerVols(): void {
        this.volService.getAllVols().subscribe({
            next: (data) => {
                this.vols = data??[];
                console.log(data);
            },
            error: () => {
                alert('Impossible de charger les vols. Vérifiez que le backend est lancé.');
            }
        });
    }

    private initialiserOrigineAnways(): void {
        if (this.anwaysAeroport) {
            this.newVol.origineId = this.anwaysAeroport.id ?? undefined;
        }
    }

    chargerPistes(): void {
        this.pisteService.getAllPistes().subscribe({
            next: (data) => {
                this.pistesAtterissages = data??[];
                this.pistesDecollages = data??[];
                console.log(data);
            },
            error: () => {
                alert('Impossible de charger les pistes. Vérifiez que le backend est lancé.');
            }
        });
    }

    chargerAeroports(): void {
        this.aeroportService.getAllAeroports().subscribe({
            next: (data) => {
                this.aeroports = data ?? [];
                console.log(data);
                this.anwaysAeroport =
                    this.aeroports.find(a =>
                        a.codeIATA === 'AWY' || a.nom === 'Anways'
                    ) ?? null;
                this.initialiserOrigineAnways();
            },
            error: () => {
                alert('Impossible de charger les aéroports. Vérifiez que le backend est lancé.');
            }
        });
    }

    ajouterVol(): void {
        if (!this.newVol.numeroVol) {
            alert('Veuillez remplir tous les champs obligatoires.');
            return;
        }
        if (this.newVol.destinationId == null) {
            alert('Veuillez sélectionner un aéroport de destination.');
            return;
        }
        if (this.newVol.origineId == null) {
            alert('L\'aéroport central Anways (code IATA AWY) est introuvable.');
            return;
        }

        const createRequest: CreateVolRequest = {
            numeroVol: this.newVol.numeroVol,
            compagnie: this.newVol.compagnie,
            origineId: this.newVol.origineId,
            destinationId: this.newVol.destinationId,
            dateDepart: this.newVol.dateDepart,
            dateArrivee: this.newVol.dateArrivee,
            typeVol: this.newVol.typeVol,
            statut: this.newVol.statut,
            avionId: this.newVol.avionId,
            pisteDecollageId:  this.newVol.pisteDecollageId,
            pisteAtterissageId:  this.newVol.pisteAtterissageId,
        };

        this.volService.createVol(createRequest).subscribe({
            next: (volCree) => {
                this.vols.push(volCree);
                this.newVol = {
                    id: 0,
                    numeroVol: '',
                    compagnie: '',
                    origineId: undefined,
                    destinationId: undefined,
                    dateDepart: new Date(),
                    dateArrivee: new Date(),
                    typeVol: 'SURCLASSE',
                    statut: 'ENATTENTE',
                    avionId: undefined,
                    pisteDecollageId: undefined,
                    pisteAtterissageId:  undefined
                };
                this.initialiserOrigineAnways();
            },
            error: (err) => {
                const backendMessage =
                    err?.error?.error ??
                    err?.error?.message ??
                    err?.error ??
                    err?.message;
                alert(backendMessage || 'Impossible de créer le vol.');
            }
        });
    }

    modifierVol(id: number): void {
        this.editingId = id;

        const vol = this.vols.find(vol => vol.id === id);
        if (vol) {
            this.originalVol = { ...vol };
            vol.dateDepart = this.toDatetimeLocalInput(vol.dateDepart) as unknown as Date;
            vol.dateArrivee = this.toDatetimeLocalInput(vol.dateArrivee) as unknown as Date;
        }
    }

    sauvegarderModification(): void {
        if (this.editingId === null) return;

        const volIndex = this.vols.findIndex(v => v.id === this.editingId);

        if (volIndex === -1) return;

        const courant = this.vols[volIndex];

        const updateRequest: UpdateVolRequest = {
            numeroVol: courant.numeroVol,
            compagnie: courant.compagnie,
            origineId: courant.origineId,
            destinationId: courant.destinationId,
            dateDepart: courant.dateDepart,
            dateArrivee: courant.dateArrivee,
            statut: courant.statut,
            typeVol: courant.typeVol,
            avionId : courant.avionId,
            pisteDecollageId : courant.pisteDecollageId,
            pisteAtterissageId : courant.pisteAtterissageId
        };

        this.volService.updateVol(this.editingId, updateRequest).subscribe({
            next: (volModifie) => {
                const index = this.vols.findIndex(v => v.id === this.editingId);
                if (index !== -1) {
                    this.vols[index] = volModifie;
                }
                this.editingId = null;
                this.originalVol = null;
            },
            error: () => {
                alert('Impossible de modifier le vol.');
            }
        });
    }

    annulerModification(): void {
        if (this.editingId !== null && this.originalVol) {
            const index = this.vols.findIndex(v => v.id === this.editingId);
            if (index !== -1) {
                this.vols[index] = { ...this.originalVol };
            }
        }

        this.editingId = null;
        this.originalVol = null;
    }

    getAeroportCodeById(id?: number | null): string | number | undefined {
        if (id === undefined) {
            return undefined;
        }
        if (id === null) {
            return '';
        }
        const aeroport = this.aeroports.find(a => a.id === id);
        return aeroport ? aeroport.codeIATA : id;
    }

    getAvionLabelById(id?: number | null): string {
        if (id == null) {
            return '';
        }
        const avion = this.avions.find(a => a.id === id);
        return avion ? `avion ${avion.id} (${avion.nom})` : `avion ${id}`;
    }

    getPisteLabelById(id?: number | null): string {
        if (id == null) {
            return '';
        }
        const piste =
            this.pistesAtterissages.find(p => p.id === id) ||
            this.pistesDecollages.find(p => p.id === id);
        return piste ? `Piste ${piste.id} (${piste.etat})` : `Piste ${id}`;
    }
}

