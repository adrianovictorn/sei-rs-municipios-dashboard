import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { MunicipioDetail, MunicipioRequest, MunicipioSummary } from '../models/municipio.model';

@Injectable({ providedIn: 'root' })
export class MunicipioService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = '/api/municipios';

  listar(): Observable<MunicipioSummary[]> {
    return this.http.get<MunicipioSummary[]>(this.baseUrl);
  }

  obter(id: number): Observable<MunicipioDetail> {
    return this.http.get<MunicipioDetail>(`${this.baseUrl}/${id}`);
  }

  criar(request: MunicipioRequest): Observable<MunicipioDetail> {
    return this.http.post<MunicipioDetail>(this.baseUrl, request);
  }

  atualizar(id: number, request: MunicipioRequest): Observable<MunicipioDetail> {
    return this.http.put<MunicipioDetail>(`${this.baseUrl}/${id}`, request);
  }

  excluir(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
