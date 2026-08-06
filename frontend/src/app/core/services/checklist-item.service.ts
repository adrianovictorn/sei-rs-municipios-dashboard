import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { ChecklistItem, ChecklistItemRequest, ChecklistItemUpdateRequest } from '../models/checklist-item.model';

@Injectable({ providedIn: 'root' })
export class ChecklistItemService {
  private readonly http = inject(HttpClient);

  adicionar(etapaId: number, request: ChecklistItemRequest): Observable<ChecklistItem> {
    return this.http.post<ChecklistItem>(`/api/etapas/${etapaId}/checklist-items`, request);
  }

  atualizar(itemId: number, request: ChecklistItemUpdateRequest): Observable<ChecklistItem> {
    return this.http.put<ChecklistItem>(`/api/checklist-items/${itemId}`, request);
  }

  excluir(itemId: number): Observable<void> {
    return this.http.delete<void>(`/api/checklist-items/${itemId}`);
  }
}
