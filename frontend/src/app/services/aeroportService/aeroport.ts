import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Aeroport} from '../../data/Aeroport';

export interface CreateAeroportRequest {
  nom: string;
  ville: string;
  pays: string;
  codeIATA: string;
}

export interface UpdateAeroportRequest {
  nom?: string;
  ville?: string;
  pays?: string;
  codeIATA?: string;
}

@Injectable({
  providedIn: 'root'
})
export class AeroportService {
  private readonly ENDPOINT_AEROPORTS = '/api/internal-AWY/aeroports';

  constructor(private httpClient: HttpClient) {}

  getAllAeroports(): Observable<Aeroport[]> {
    return this.httpClient.get<Aeroport[]>(`${this.ENDPOINT_AEROPORTS}`);
  }

  getAeroportById(id: number): Observable<Aeroport> {
    return this.httpClient.get<Aeroport>(`${this.ENDPOINT_AEROPORTS}/${id}`);
  }

  createAeroport(dto: CreateAeroportRequest): Observable<Aeroport> {
    return this.httpClient.post<Aeroport>(`${this.ENDPOINT_AEROPORTS}`, dto);
  }

  updateAeroport(id: number, dto: UpdateAeroportRequest): Observable<Aeroport> {
    return this.httpClient.put<Aeroport>(`${this.ENDPOINT_AEROPORTS}/${id}`, dto);
  }

  deleteAeroport(id: number): Observable<void> {
    return this.httpClient.delete<void>(`${this.ENDPOINT_AEROPORTS}/${id}`);
  }

  searchByCodeIATA(codeIATA: string): Observable<Aeroport> {
    return this.httpClient.get<Aeroport>(
      `${this.ENDPOINT_AEROPORTS}/code/${codeIATA}`
    );
  }
}

