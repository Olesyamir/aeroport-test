import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Hangar } from '../../data/Hangar';

export interface CreateHangarRequest {
  id: number;
  capacite: number;
  etat: string;
}

export interface UpdateHangarRequest {
  capacite?: number;
  etat?: string;
}

@Injectable({
  providedIn: 'root'
})
export class HangarService {
  private readonly ENDPOINT_HANGARS = '/api/internal-AWY/hangars';

  constructor(private httpClient: HttpClient) {}

  getAllHangars(): Observable<Hangar[]> {
    return this.httpClient.get<Hangar[]>(`${this.ENDPOINT_HANGARS}`);
  }

  getHangarById(id: number): Observable<Hangar> {
    return this.httpClient.get<Hangar>(`${this.ENDPOINT_HANGARS}/${id}`);
  }

  createHangar(dto: CreateHangarRequest): Observable<Hangar> {
    return this.httpClient.post<Hangar>(`${this.ENDPOINT_HANGARS}`, dto);
  }

  updateHangar(id: number, dto: UpdateHangarRequest): Observable<Hangar> {
    return this.httpClient.put<Hangar>(`${this.ENDPOINT_HANGARS}/${id}`, dto);
  }

  deleteHangar(id: number): Observable<void> {
    return this.httpClient.delete<void>(`${this.ENDPOINT_HANGARS}/${id}`);
  }
}
