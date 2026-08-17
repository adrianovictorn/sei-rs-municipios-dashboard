import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Equipe, EquipeRequest } from '../models/equipe.model';

@Injectable({ providedIn: 'root' })
export class EquipeService {
  private readonly http = inject(HttpClient);

  listar(): Observable<Equipe[]> {
    return this.http.get<Equipe[]>('/api/equipes');
  }

  criar(request: EquipeRequest): Observable<Equipe> {
    return this.http.post<Equipe>('/api/equipes', request);
  }

  atualizar(id: number, request: EquipeRequest): Observable<Equipe> {
    return this.http.put<Equipe>(`/api/equipes/${id}`, request);
  }

  excluir(id: number): Observable<void> {
    return this.http.delete<void>(`/api/equipes/${id}`);
  }
}
