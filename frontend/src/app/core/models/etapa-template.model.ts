export interface ChecklistItemTemplate {
  id: number;
  descricao: string;
  ordem: number;
  duracaoDias: number | null;
}

export interface ChecklistItemTemplateRequest {
  descricao: string;
  ordem?: number;
  duracaoDias?: number;
}

export interface EtapaTemplate {
  id: number;
  nome: string;
  descricao: string | null;
  ordem: number;
  duracaoDias: number | null;
  itens: ChecklistItemTemplate[];
}

export interface EtapaTemplateRequest {
  nome: string;
  descricao?: string;
  ordem?: number;
  duracaoDias?: number;
}
