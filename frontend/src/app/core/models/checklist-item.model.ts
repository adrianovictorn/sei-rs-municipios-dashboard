export interface ChecklistItem {
  id: number;
  descricao: string;
  concluido: boolean;
  dataConclusao: string | null;
  ordem: number;
  templateId: number | null;
  dataInicio: string | null;
  dataFim: string | null;
  duracaoDias: number | null;
  percentualPrevisto: number | null;
  predecessoras: string | null;
}

export interface ChecklistItemRequest {
  descricao: string;
  ordem?: number;
  dataInicio?: string;
  dataFim?: string;
  duracaoDias?: number;
  percentualPrevisto?: number;
  predecessoras?: string;
}

export interface ChecklistItemUpdateRequest {
  descricao?: string;
  concluido?: boolean;
  ordem?: number;
  dataInicio?: string;
  dataFim?: string;
  duracaoDias?: number;
  percentualPrevisto?: number;
  predecessoras?: string;
}
