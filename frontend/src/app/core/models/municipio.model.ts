import { Etapa } from './etapa.model';

export interface MunicipioSummary {
  id: number;
  nome: string;
  regiao: string | null;
  dataInicio: string | null;
  dataPrevistaGolive: string | null;
  progresso: number;
  totalEtapas: number;
  etapasConcluidas: number;
  etapaAtual: string | null;
  equipeId: number | null;
  equipeNome: string | null;
  parado: boolean;
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
  dataPrevistaGolive: string | null;
  observacoes: string | null;
  equipeId: number | null;
  equipeNome: string | null;
  parado: boolean;
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
  dataPrevistaGolive?: string;
  observacoes?: string;
  equipeId?: number;
  parado?: boolean;
}
