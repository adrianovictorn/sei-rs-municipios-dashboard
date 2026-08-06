import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Etapa, EtapaRequest } from '../models/etapa.model';

@Injectable({ providedIn: 'root' })
export class EtapaService {
  private readonly http = inject(HttpClient);

  adicionar(municipioId: number, request: EtapaRequest): Observable<Etapa> {
    return this.http.post<Etapa>(`/api/municipios/${municipioId}/etapas`, request);
  }

  atualizar(etapaId: number, request: EtapaRequest): Observable<Etapa> {
    return this.http.put<Etapa>(`/api/etapas/${etapaId}`, request);
  }

  excluir(etapaId: number): Observable<void> {
    return this.http.delete<void>(`/api/etapas/${etapaId}`);
  }
}
