import { Injectable } from '@angular/core';
import {Statut} from "../../data/Statut";
import {TypeVol} from "../../data/TypeVol";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {Vol} from "../../data/Vol";


export interface CreateVolRequest {
  numeroVol: String,
  compagnie: String,
  origineId: number,
  destinationId: number,
  dateDepart: Date,
  dateArrivee: Date,
  statut: Statut,
  typeVol: TypeVol,
  avionId?: number,
  pisteDecollageId?: number,
  pisteAtterissageId?: number,
}

export interface UpdateVolRequest {
  numeroVol?: String,
  compagnie?: String,
  origineId?: number,
  destinationId?: number,
  dateDepart?: Date,
  dateArrivee?: Date,
  statut?: Statut,
  typeVol?: TypeVol,
  avionId?: number,
  pisteDecollageId?: number,
  pisteAtterissageId?: number,
}


@Injectable({
  providedIn: 'root'
})
export class VolServivce {
  private readonly ENDPOINT_AVIONS = '/api/vols';


  constructor(private httpClient: HttpClient) {}

  getAllVols(): Observable<Vol[]> {
    return this.httpClient.get<Vol[]>(`${this.ENDPOINT_AVIONS}`);
  }


  getVolById(id: number): Observable<Vol> {
    return this.httpClient.get<Vol>(`${this.ENDPOINT_AVIONS}/${id}`);
  }


  createVol(dto: CreateVolRequest): Observable<Vol> {
    return this.httpClient.post<Vol>(`${this.ENDPOINT_AVIONS}`, dto);
  }


  updateVol(id: number, dto: UpdateVolRequest): Observable<Vol> {
    return this.httpClient.put<Vol>(`${this.ENDPOINT_AVIONS}/${id}`, dto);
  }


  deleteVol(id: number): Observable<void> {
    return this.httpClient.delete<void>(`${this.ENDPOINT_AVIONS}/${id}`);
  }


  findStatutById(id: number): Observable<Statut> {
    return this.httpClient.get<Statut>(`${this.ENDPOINT_AVIONS}/${id}/statut`);
  }


  getVolsParStatut(statut: Statut): Observable<Vol[]> {
    return this.httpClient.get<Vol[]>(`${this.ENDPOINT_AVIONS}/statut/${statut}`);
  }

}
