import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Piste } from '../../data/Piste';
import {EtatPiste} from '../../data/EtatPiste';

export interface CreatePisteRequest {
  longueur: number;
  etat: EtatPiste;
}

export interface UpdatePisteRequest {
  longueur?: number;
  etat?: EtatPiste;
}

@Injectable({
  providedIn: 'root'
})
export class PisteService {
  private readonly ENDPOINT_PISTES = '/api/internal-AWY/pistes';

  constructor(private httpClient: HttpClient) {}

  getAllPistes(): Observable<Piste[]> {
    return this.httpClient.get<Piste[]>(`${this.ENDPOINT_PISTES}`);
  }

  getPisteById(id: number): Observable<Piste> {
    return this.httpClient.get<Piste>(`${this.ENDPOINT_PISTES}/${id}`);
  }

  createPiste(dto: CreatePisteRequest): Observable<Piste> {
    return this.httpClient.post<Piste>(`${this.ENDPOINT_PISTES}`, dto);
  }

  updatePiste(id: number, dto: UpdatePisteRequest): Observable<Piste> {
    return this.httpClient.put<Piste>(`${this.ENDPOINT_PISTES}/${id}`, dto);
  }

  deletePiste(id: number): Observable<void> {
    return this.httpClient.delete<void>(`${this.ENDPOINT_PISTES}/${id}`);
  }
}
