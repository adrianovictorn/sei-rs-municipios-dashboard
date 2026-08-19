export interface Agenda {
  id: number;
  municipioId: number;
  municipioNome: string;
  tipoAgendaId: number | null;
  tipoAgendaNome: string | null;
  titulo: string;
  dataHora: string;
  local: string | null;
  observacoes: string | null;
  realizada: boolean;
}

export interface AgendaRequest {
  tipoAgendaId?: number;
  titulo: string;
  dataHora: string;
  local?: string;
  observacoes?: string;
  realizada?: boolean;
}
