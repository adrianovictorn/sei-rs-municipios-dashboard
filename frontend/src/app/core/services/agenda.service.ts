import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Agenda, AgendaRequest } from '../models/agenda.model';

@Injectable({ providedIn: 'root' })
export class AgendaService {
  private readonly http = inject(HttpClient);

  listar(): Observable<Agenda[]> {
    return this.http.get<Agenda[]>('/api/agendas');
  }

  criar(municipioId: number, request: AgendaRequest): Observable<Agenda> {
    return this.http.post<Agenda>(`/api/municipios/${municipioId}/agendas`, request);
  }

  atualizar(id: number, request: AgendaRequest): Observable<Agenda> {
    return this.http.put<Agenda>(`/api/agendas/${id}`, request);
  }

  excluir(id: number): Observable<void> {
    return this.http.delete<void>(`/api/agendas/${id}`);
  }
}
