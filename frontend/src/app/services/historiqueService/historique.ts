import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {Historique} from "../../data/Historique";
import {Statut} from "../../data/Statut";

export interface CreateHistoriquesRequest {
  statut : Statut
  idVol : number
}
@Injectable({
  providedIn: 'root'
})
export class HistoriqueService {
  private readonly ENDPOINT_HISTORIQUES = "/api/internal-AWY/historiques";

  constructor(private httpClient: HttpClient) {}

  getAllHistoriques(): Observable<Historique[]>{
    return  this.httpClient.get<Historique[]>( `${this.ENDPOINT_HISTORIQUES}`);
  }

  getHistoriquesById(id: number): Observable<Historique> {
    return this.httpClient.get<Historique>(`${this.ENDPOINT_HISTORIQUES}/${id}`);
  }

  createHistoriques(dto: CreateHistoriquesRequest): Observable<Historique> {
    return this.httpClient.post<Historique>(`${this.ENDPOINT_HISTORIQUES}`, dto);
  }

  searchByIDVol(idVol: number ) : Observable<Historique> {
    return this.httpClient.get<Historique>(`${this.ENDPOINT_HISTORIQUES}/idVol/${idVol}`)
  }

  deleteHistoriqueById(id : number) : Observable<void>{
     return this.httpClient.delete<void>(`${this.ENDPOINT_HISTORIQUES}/${id}`)
  }
}
