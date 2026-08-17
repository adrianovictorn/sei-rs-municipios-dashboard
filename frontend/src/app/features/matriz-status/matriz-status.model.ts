import { Etapa } from '../../core/models/etapa.model';

export type BadgeTipo = 'SIM' | 'NAO' | 'PARCIAL' | 'SOLICITADO';

export interface Previsao {
  data: string;
  atrasado: boolean;
}

export interface CelulaStatus {
  tipo: BadgeTipo;
  percentual: number;
  dataSolicitacao: string | null;
  previsao: Previsao | null;
  etapa: Etapa | null;
}

export function calcularCelula(etapa: Etapa | undefined): CelulaStatus {
  if (!etapa) {
    return { tipo: 'NAO', percentual: 0, dataSolicitacao: null, previsao: null, etapa: null };
  }

  const percentual = etapa.progresso;
  let tipo: BadgeTipo;
  if (percentual >= 100) {
    tipo = 'SIM';
  } else if (percentual === 0 && etapa.dataSolicitacao) {
    tipo = 'SOLICITADO';
  } else if (percentual === 0) {
    tipo = 'NAO';
  } else {
    tipo = 'PARCIAL';
  }

  let previsao: Previsao | null = null;
  if (percentual < 100 && etapa.dataFim) {
    const hoje = new Date().toISOString().slice(0, 10);
    previsao = { data: etapa.dataFim, atrasado: etapa.dataFim < hoje };
  }

  return { tipo, percentual, dataSolicitacao: etapa.dataSolicitacao, previsao, etapa };
}

export function formatarDataBr(iso: string | null): string {
  if (!iso) {
    return '';
  }
  const [ano, mes, dia] = iso.split('-');
  return `${dia}/${mes}`;
}
