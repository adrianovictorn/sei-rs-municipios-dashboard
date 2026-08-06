import { Etapa } from './etapa.model';

export interface MunicipioSummary {
  id: number;
  nome: string;
  regiao: string | null;
  dataInicio: string | null;
  progresso: number;
  totalEtapas: number;
  etapasConcluidas: number;
  etapaAtual: string | null;
}

export interface MunicipioDetail {
  id: number;
  nome: string;
  codigoIbge: string | null;
  regiao: string | null;
  populacao: number | null;
  patrocinadorExecutivo: string | null;
  pontoFocalNome: string | null;
  pontoFocalEmail: string | null;
  pontoFocalTelefone: string | null;
  dataInicio: string | null;
  observacoes: string | null;
  progresso: number;
  createdAt: string;
  updatedAt: string;
  etapas: Etapa[];
}

export interface MunicipioRequest {
  nome: string;
  codigoIbge?: string;
  regiao?: string;
  populacao?: number;
  patrocinadorExecutivo?: string;
  pontoFocalNome?: string;
  pontoFocalEmail?: string;
  pontoFocalTelefone?: string;
  dataInicio?: string;
  observacoes?: string;
}
