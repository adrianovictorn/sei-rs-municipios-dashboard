import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { TipoAgenda, TipoAgendaRequest } from '../models/tipo-agenda.model';

@Injectable({ providedIn: 'root' })
export class TipoAgendaService {
  private readonly http = inject(HttpClient);

  listar(): Observable<TipoAgenda[]> {
    return this.http.get<TipoAgenda[]>('/api/tipos-agenda');
  }

  criar(request: TipoAgendaRequest): Observable<TipoAgenda> {
    return this.http.post<TipoAgenda>('/api/tipos-agenda', request);
  }

  atualizar(id: number, request: TipoAgendaRequest): Observable<TipoAgenda> {
    return this.http.put<TipoAgenda>(`/api/tipos-agenda/${id}`, request);
  }

  excluir(id: number): Observable<void> {
    return this.http.delete<void>(`/api/tipos-agenda/${id}`);
  }
}
