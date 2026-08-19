import { Component, DestroyRef, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { forkJoin, interval } from 'rxjs';
import { MunicipioService } from '../../core/services/municipio.service';
import { EtapaTemplateService } from '../../core/services/etapa-template.service';
import { MunicipioDetail, MunicipioRequest } from '../../core/models/municipio.model';
import { EtapaTemplate } from '../../core/models/etapa-template.model';
import { CelulaStatus, calcularCelula, formatarDataBr, formatarDataCompletaBr } from './matriz-status.model';

type SortBy = 'prazo' | 'percentual' | 'equipe' | 'atraso';

const AUTO_REFRESH_MS = 30000;

const BADGE_CLASSE: Record<CelulaStatus['tipo'], string> = {
  SIM: 'bg-brand-green text-white',
  NAO: 'bg-brand-red text-white',
  PARCIAL: 'bg-brand-yellow text-white',
  SOLICITADO: 'bg-brand-blue text-white'
};

const BADGE_ARGB: Record<CelulaStatus['tipo'], string> = {
  SIM: 'FF6AA42D',
  NAO: 'FFD31145',
  PARCIAL: 'FFF5A800',
  SOLICITADO: 'FF1E73BE'
};

@Component({
  selector: 'app-matriz-status',
  standalone: true,
  imports: [RouterLink, FormsModule],
  templateUrl: './matriz-status.html'
})
export class MatrizStatus {
  private readonly municipioService = inject(MunicipioService);
  private readonly etapaTemplateService = inject(EtapaTemplateService);
  private readonly destroyRef = inject(DestroyRef);

  colunas = signal<EtapaTemplate[]>([]);
  municipios = signal<MunicipioDetail[]>([]);
  carregando = signal(true);
  erro = signal<string | null>(null);
  ultimaAtualizacao = signal<Date | null>(null);
  exportando = signal(false);

  expandedCell = signal<string | null>(null);
  hojeIso = new Date().toISOString().slice(0, 10);

  sortBy = signal<SortBy>('prazo');
  filtroEquipe = signal<string>('todas');
  somenteAtrasados = signal(false);

  equipesDisponiveis = computed(() => {
    const nomes = new Set<string>();
    for (const m of this.municipios()) {
      if (m.equipeNome) {
        nomes.add(m.equipeNome);
      }
    }
    return Array.from(nomes).sort((a, b) => a.localeCompare(b));
  });

  linhas = computed(() => {
    const colunas = this.colunas();
    let lista = this.municipios();

    if (this.filtroEquipe() !== 'todas') {
      lista = lista.filter((m) => m.equipeNome === this.filtroEquipe());
    }
    if (this.somenteAtrasados()) {
      lista = lista.filter((m) => colunas.some((c) => this.celula(m, c).previsao?.atrasado));
    }

    const percentualMedio = (m: MunicipioDetail): number => {
      if (colunas.length === 0) {
        return 0;
      }
      return colunas.reduce((acc, c) => acc + this.celula(m, c).percentual, 0) / colunas.length;
    };
    const prazoMaisProximo = (m: MunicipioDetail): number => {
      const tempos = colunas
        .map((c) => this.celula(m, c))
        .filter((cel) => cel.percentual < 100 && cel.previsao)
        .map((cel) => new Date(cel.previsao!.data).getTime());
      return tempos.length > 0 ? Math.min(...tempos) : Number.POSITIVE_INFINITY;
    };
    const temAtraso = (m: MunicipioDetail): number =>
      colunas.some((c) => this.celula(m, c).previsao?.atrasado) ? 0 : 1;

    const ordenada = [...lista];
    switch (this.sortBy()) {
      case 'prazo':
        ordenada.sort((a, b) => prazoMaisProximo(a) - prazoMaisProximo(b));
        break;
      case 'percentual':
        ordenada.sort((a, b) => percentualMedio(b) - percentualMedio(a));
        break;
      case 'equipe':
        ordenada.sort((a, b) => (a.equipeNome ?? '').localeCompare(b.equipeNome ?? ''));
        break;
      case 'atraso':
        ordenada.sort((a, b) => temAtraso(a) - temAtraso(b));
        break;
    }
    return ordenada;
  });

  constructor() {
    this.carregar();
    const assinatura = interval(AUTO_REFRESH_MS).subscribe(() => this.carregar(true));
    this.destroyRef.onDestroy(() => assinatura.unsubscribe());
  }

  carregar(silencioso = false): void {
    if (!silencioso) {
      this.carregando.set(true);
    }
    forkJoin({
      colunas: this.etapaTemplateService.listar(),
      resumos: this.municipioService.listar()
    }).subscribe({
      next: ({ colunas, resumos }) => {
        this.colunas.set(
          colunas
            .filter((c) => c.exibirMatriz)
            .sort((a, b) => (a.ordemMatriz ?? 0) - (b.ordemMatriz ?? 0))
        );

        if (resumos.length === 0) {
          this.municipios.set([]);
          this.carregando.set(false);
          this.ultimaAtualizacao.set(new Date());
          return;
        }

        forkJoin(resumos.map((r) => this.municipioService.obter(r.id))).subscribe((detalhados) => {
          this.municipios.set(detalhados);
          this.carregando.set(false);
          this.ultimaAtualizacao.set(new Date());
        });
      },
      error: () => {
        this.erro.set('Não foi possível carregar a matriz de status.');
        this.carregando.set(false);
      }
    });
  }

  celula(municipio: MunicipioDetail, coluna: EtapaTemplate): CelulaStatus {
    const etapa = municipio.etapas.find((e) => e.templateId === coluna.id);
    return calcularCelula(etapa);
  }

  goLiveAtrasado(municipio: MunicipioDetail): boolean {
    return !!municipio.dataPrevistaGolive && municipio.dataPrevistaGolive < this.hojeIso && municipio.progresso < 100;
  }

  formatarGoLive(municipio: MunicipioDetail): string {
    return formatarDataCompletaBr(municipio.dataPrevistaGolive);
  }

  badgeClasse(cel: CelulaStatus): string {
    return BADGE_CLASSE[cel.tipo];
  }

  badgeTexto(cel: CelulaStatus): string {
    switch (cel.tipo) {
      case 'SIM':
        return 'SIM';
      case 'NAO':
        return 'NÃO';
      case 'PARCIAL':
        return `PARCIAL ${cel.percentual}%`;
      case 'SOLICITADO':
        return `Solicitado ${formatarDataBr(cel.dataSolicitacao)}`;
    }
  }

  previsaoTexto(cel: CelulaStatus): string {
    if (!cel.previsao) {
      return '';
    }
    return (cel.previsao.atrasado ? '⚠ ' : '📅 ') + formatarDataBr(cel.previsao.data);
  }

  previsaoClasse(cel: CelulaStatus): string {
    return cel.previsao?.atrasado
      ? 'border border-brand-red text-brand-red animate-pulse'
      : 'border border-brand-blue/40 text-brand-blue';
  }

  toggleCelula(municipioId: number, colunaId: number): void {
    const chave = `${municipioId}:${colunaId}`;
    this.expandedCell.set(this.expandedCell() === chave ? null : chave);
  }

  celulaExpandida(municipioId: number, colunaId: number): boolean {
    return this.expandedCell() === `${municipioId}:${colunaId}`;
  }

  salvarObservacao(municipio: MunicipioDetail, texto: string): void {
    if (texto === (municipio.observacoes ?? '')) {
      return;
    }
    const request: MunicipioRequest = {
      nome: municipio.nome,
      codigoIbge: municipio.codigoIbge ?? undefined,
      regiao: municipio.regiao ?? undefined,
      populacao: municipio.populacao ?? undefined,
      patrocinadorExecutivo: municipio.patrocinadorExecutivo ?? undefined,
      pontoFocalNome: municipio.pontoFocalNome ?? undefined,
      pontoFocalEmail: municipio.pontoFocalEmail ?? undefined,
      pontoFocalTelefone: municipio.pontoFocalTelefone ?? undefined,
      dataInicio: municipio.dataInicio ?? undefined,
      dataPrevistaGolive: municipio.dataPrevistaGolive ?? undefined,
      observacoes: texto,
      equipeId: municipio.equipeId ?? undefined,
      parado: municipio.parado
    };
    this.municipioService.atualizar(municipio.id, request).subscribe(() => this.carregar(true));
  }

  exportarPdf(): void {
    window.print();
  }

  async exportarExcel(): Promise<void> {
    this.exportando.set(true);
    try {
      const ExcelJS = await import('exceljs');
      const workbook = new ExcelJS.Workbook();
      const sheet = workbook.addWorksheet('Matriz de status');
      const colunas = this.colunas();

      sheet.addRow(['Município', 'Equipe', ...colunas.map((c) => c.nome), 'Go-live previsto', 'Observação']);
      const cabecalho = sheet.getRow(1);
      cabecalho.font = { bold: true, color: { argb: 'FFFFFFFF' } };
      cabecalho.eachCell((cell) => {
        cell.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'FF0F2A52' } };
      });

      for (const municipio of this.linhas()) {
        const row = sheet.addRow([
          municipio.nome,
          municipio.parado ? 'parado' : municipio.equipeNome ?? '',
          ...colunas.map((c) => this.badgeTexto(this.celula(municipio, c))),
          formatarDataCompletaBr(municipio.dataPrevistaGolive),
          municipio.observacoes ?? ''
        ]);

        if (municipio.parado) {
          row.getCell(2).font = { color: { argb: 'FFD31145' }, bold: true };
        }

        colunas.forEach((coluna, index) => {
          const cel = this.celula(municipio, coluna);
          const cell = row.getCell(3 + index);
          cell.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: BADGE_ARGB[cel.tipo] } };
          cell.font = { color: { argb: 'FFFFFFFF' } };
        });
      }

      sheet.columns.forEach((col) => {
        col.width = 22;
      });

      const buffer = await workbook.xlsx.writeBuffer();
      const blob = new Blob([buffer], {
        type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
      });
      const url = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `matriz-status-${new Date().toISOString().slice(0, 10)}.xlsx`;
      a.click();
      URL.revokeObjectURL(url);
    } finally {
      this.exportando.set(false);
    }
  }
}
