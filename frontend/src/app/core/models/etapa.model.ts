import { ChecklistItem } from './checklist-item.model';

export type EtapaStatus = 'NAO_INICIADA' | 'EM_ANDAMENTO' | 'CONCLUIDA';

export interface Etapa {
  id: number;
  nome: string;
  descricao: string | null;
  ordem: number;
  progresso: number;
  status: EtapaStatus;
  templateId: number | null;
  dataInicio: string | null;
  dataFim: string | null;
  duracaoDias: number | null;
  percentualPrevisto: number | null;
  predecessoras: string | null;
  checklistItems: ChecklistItem[];
}

export interface EtapaRequest {
  nome: string;
  descricao?: string;
  ordem?: number;
  dataInicio?: string;
  dataFim?: string;
  duracaoDias?: number;
  percentualPrevisto?: number;
  predecessoras?: string;
}
