export interface ChecklistItem {
  id: number;
  descricao: string;
  concluido: boolean;
  dataConclusao: string | null;
  ordem: number;
}

export interface ChecklistItemRequest {
  descricao: string;
  ordem?: number;
}

export interface ChecklistItemUpdateRequest {
  descricao?: string;
  concluido?: boolean;
  ordem?: number;
}
