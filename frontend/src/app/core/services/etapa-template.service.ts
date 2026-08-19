import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import {
  ChecklistItemTemplate,
  ChecklistItemTemplateRequest,
  EtapaTemplate,
  EtapaTemplateRequest
} from '../models/etapa-template.model';

@Injectable({ providedIn: 'root' })
export class EtapaTemplateService {
  private readonly http = inject(HttpClient);

  listar(): Observable<EtapaTemplate[]> {
    return this.http.get<EtapaTemplate[]>('/api/etapa-templates');
  }

  adicionarFase(request: EtapaTemplateRequest): Observable<EtapaTemplate> {
    return this.http.post<EtapaTemplate>('/api/etapa-templates', request);
  }

  atualizarFase(id: number, request: EtapaTemplateRequest): Observable<EtapaTemplate> {
    return this.http.put<EtapaTemplate>(`/api/etapa-templates/${id}`, request);
  }

  excluirFase(id: number, emTodosMunicipios = false): Observable<void> {
    return this.http.delete<void>(`/api/etapa-templates/${id}`, {
      params: { emTodosMunicipios }
    });
  }

  adicionarTarefa(faseId: number, request: ChecklistItemTemplateRequest): Observable<ChecklistItemTemplate> {
    return this.http.post<ChecklistItemTemplate>(`/api/etapa-templates/${faseId}/itens`, request);
  }

  atualizarTarefa(id: number, request: ChecklistItemTemplateRequest): Observable<ChecklistItemTemplate> {
    return this.http.put<ChecklistItemTemplate>(`/api/checklist-item-templates/${id}`, request);
  }

  excluirTarefa(id: number, emTodosMunicipios = false): Observable<void> {
    return this.http.delete<void>(`/api/checklist-item-templates/${id}`, {
      params: { emTodosMunicipios }
    });
  }
}
