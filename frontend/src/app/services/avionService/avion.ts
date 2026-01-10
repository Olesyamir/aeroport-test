import { Injectable } from '@angular/core';
import {EtatMateriel} from '../../data/EtatMateriel';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Avion} from '../../data/Avion';
import {Vol} from "../../data/Vol";


export interface CreateAvionRequest {
  nom: string;
  numImmatricule: string;
  type: string;
  capacite: number;
  etat: EtatMateriel;
  hangarId?: number;
  volIds?: number[]
}

export interface UpdateAvionRequest {
  nom?: string;
  numImmatricule?: string;
  type?: string;
  capacite?: number;
  etat?: EtatMateriel;
  hangarId?: number | null;
  volsId?: number[] | undefined;
}

@Injectable({ providedIn: 'root' })
export class AvionServivce {
  private readonly ENDPOINT_AVIONS = '/api/internal-AWY/avions';

  constructor(private httpClient: HttpClient) {}

  getAllAvions(): Observable<Avion[]> {
    return this.httpClient.get<Avion[]>(`${this.ENDPOINT_AVIONS}`);
  }


  getAvionById(id: number): Observable<Avion> {
    return this.httpClient.get<Avion>(`${this.ENDPOINT_AVIONS}/${id}`);
  }


  createAvion(dto: CreateAvionRequest): Observable<Avion> {
    return this.httpClient.post<Avion>(`${this.ENDPOINT_AVIONS}`, dto);
  }


  updateAvion(id: number, dto: UpdateAvionRequest): Observable<Avion> {
    return this.httpClient.put<Avion>(`${this.ENDPOINT_AVIONS}/${id}`, dto);
  }



  deleteAvion(id: number): Observable<void> {
    return this.httpClient.delete<void>(`${this.ENDPOINT_AVIONS}/${id}`);
  }


    //  { params: { numImmatricule } }

    searchByNumImmatricule(numImmatricule: string): Observable<Avion> {
      return this.httpClient.get<Avion>(
          `${this.ENDPOINT_AVIONS}/numImat/${numImmatricule}`
      );
  }
}
