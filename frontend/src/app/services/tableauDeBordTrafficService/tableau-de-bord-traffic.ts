import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Historique} from '../../data/Historique';
import {PlanningPistes} from '../../data/PlanningPistes';
import {
  TableauDeBordTraffic,
  VolTraffic
} from '../../data/TableauDeBordTrafic';
import {Statut} from '../../data/Statut';

@Injectable({
  providedIn: 'root'
})
export class TableauDeBordTrafficService {
  private readonly ENDPOINT = '/api/tableau-bord';

  constructor(private httpClient: HttpClient) {}

  getTableauDeBord(aeroportId: number): Observable<TableauDeBordTraffic> {
    return this.httpClient.get<TableauDeBordTraffic>(
      `${this.ENDPOINT}/${aeroportId}`
    );
  }

  getVolsDepart(aeroportId: number): Observable<VolTraffic[]> {
    return this.httpClient.get<VolTraffic[]>(
      `${this.ENDPOINT}/${aeroportId}/vols-depart`
    );
  }

  getVolsArrivee(aeroportId: number): Observable<VolTraffic[]> {
    return this.httpClient.get<VolTraffic[]>(
      `${this.ENDPOINT}/${aeroportId}/vols-arrivee`
    );
  }

  getHistoriquesAeroport(aeroportId: number): Observable<Historique[]> {
    return this.httpClient.get<Historique[]>(
      `${this.ENDPOINT}/${aeroportId}/historiques`
    );
  }

  getPlanningsPistesAeroport(aeroportId: number): Observable<PlanningPistes[]> {
    return this.httpClient.get<PlanningPistes[]>(
      `${this.ENDPOINT}/${aeroportId}/plannings-pistes`
    );
  }

  getVolsParStatut(aeroportId: number, statut: Statut): Observable<VolTraffic[]> {
    return this.httpClient.get<VolTraffic[]>(
      `${this.ENDPOINT}/${aeroportId}/vols-par-statut/${statut}`
    );
  }

  getVolsEnCours(aeroportId: number): Observable<VolTraffic[]> {
    return this.httpClient.get<VolTraffic[]>(
      `${this.ENDPOINT}/${aeroportId}/vols-en-cours`
    );
  }

  getVolsDepartBja(): Observable<VolTraffic[]> {
    return this.httpClient.get<VolTraffic[]>(
      `${this.ENDPOINT}/partenaires/BJA/departs`
    );
  }

  getVolsArriveeBja(): Observable<VolTraffic[]> {
    return this.httpClient.get<VolTraffic[]>(
      `${this.ENDPOINT}/partenaires/BJA/arrivees`
    );
  }
}
