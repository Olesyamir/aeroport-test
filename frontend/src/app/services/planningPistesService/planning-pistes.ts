import { Injectable } from '@angular/core';
import {Priorite} from "../../data/Priorite";
import {Usage} from "../../data/Usage";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {PlanningPistes} from "../../data/PlanningPistes";

export interface CreatePlanningPistesRequest {
  startTime: Date,
  endTime: Date,
  priorite: Priorite,
  remarques: string,
  volIds :number[],
  usage: Usage,
}

export interface UpdatePlanningPistesRequest {
  startTime: Date,
  endTime: Date,
  priorite: Priorite,
  remarques: string,
  volIds :number[],
  pisteId : number[],
  usage: Usage,
}

@Injectable({
  providedIn: 'root'
})
export class PlanningPistesServices {
  private readonly ENDPOINT_PLANNINGPISTES = '/api/internal-AWY/planningPistes';

  constructor(private httpClient: HttpClient) {}

  getAllPlanningPistes(): Observable<PlanningPistes[]> {
    return this.httpClient.get<PlanningPistes[]>(`${this.ENDPOINT_PLANNINGPISTES}`);
  }

  getPlanningPisteById(id: number): Observable<PlanningPistes> {
    return this.httpClient.get<PlanningPistes>(`${this.ENDPOINT_PLANNINGPISTES}/${id}`);
  }

  createPlanningPistes(dto: CreatePlanningPistesRequest): Observable<PlanningPistes> {
    return this.httpClient.post<PlanningPistes>(`${this.ENDPOINT_PLANNINGPISTES}`, dto);
  }

  updatePlanningPistes(id: number, dto: UpdatePlanningPistesRequest): Observable<PlanningPistes> {
    return this.httpClient.put<PlanningPistes>(`${this.ENDPOINT_PLANNINGPISTES}/${id}`, dto);
  }

  deletePlanningPistes(id: number): Observable<void> {
    return this.httpClient.delete<void>(`${this.ENDPOINT_PLANNINGPISTES}/${id}`);
  }

}
