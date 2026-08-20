import { Component, inject, signal } from '@angular/core';
import { DatePipe, DecimalPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { MunicipioService } from '../../../core/services/municipio.service';
import { EtapaService } from '../../../core/services/etapa.service';
import { ChecklistItemService } from '../../../core/services/checklist-item.service';
import { MunicipioDetail } from '../../../core/models/municipio.model';
import { Etapa, EtapaRequest } from '../../../core/models/etapa.model';
import { ProgressBar } from '../../../shared/components/progress-bar/progress-bar';
import { Roadmap } from '../../../shared/components/roadmap/roadmap';
import { StatusBadge } from '../../../shared/components/status-badge/status-badge';

@Component({
  selector: 'app-municipio-detail',
  standalone: true,
  imports: [RouterLink, FormsModule, DecimalPipe, DatePipe, ProgressBar, Roadmap, StatusBadge],
  templateUrl: './municipio-detail.html'
})
export class MunicipioDetailPage {
  private readonly route = inject(ActivatedRoute);
  private readonly municipioService = inject(MunicipioService);
  private readonly etapaService = inject(EtapaService);
  private readonly checklistItemService = inject(ChecklistItemService);

  municipioId = Number(this.route.snapshot.paramMap.get('id'));
  hojeIso = new Date().toISOString().slice(0, 10);

  municipio = signal<MunicipioDetail | null>(null);
  carregando = signal(true);
  expandedEtapaId = signal<number | null>(null);

  mostrarFormNovaEtapa = signal(false);
  novaEtapaNome = signal('');

  etapaEmEdicaoId = signal<number | null>(null);
  etapaEdicaoNome = signal('');

  novoItemTexto: Record<number, string> = {};

  mostrarResumo = signal(false);
  resumoCopiado = signal(false);

  constructor() {
    this.carregar();
  }

  carregar(): void {
    this.carregando.set(true);
    this.municipioService.obter(this.municipioId).subscribe((municipio) => {
      this.municipio.set(municipio);
      this.carregando.set(false);
      if (this.expandedEtapaId() === null && municipio.etapas.length > 0) {
        this.expandedEtapaId.set(municipio.etapas[0].id);
      }
    });
  }

  toggleEtapa(etapaId: number): void {
    this.expandedEtapaId.set(this.expandedEtapaId() === etapaId ? null : etapaId);
  }

  toggleItem(item: { id: number; concluido: boolean }): void {
    this.checklistItemService.atualizar(item.id, { concluido: !item.concluido }).subscribe(() => this.carregar());
  }

  etapaTodosConcluidos(etapa: Etapa): boolean {
    return etapa.checklistItems.length > 0 && etapa.checklistItems.every((item) => item.concluido);
  }

  alternarTodosItens(etapa: Etapa): void {
    if (etapa.checklistItems.length === 0) {
      return;
    }
    const novoValor = !this.etapaTodosConcluidos(etapa);
    const pendentes = etapa.checklistItems.filter((item) => item.concluido !== novoValor);
    if (pendentes.length === 0) {
      return;
    }
    const requisicoes = pendentes.map((item) =>
      this.checklistItemService.atualizar(item.id, { concluido: novoValor })
    );
    forkJoin(requisicoes).subscribe(() => this.carregar());
  }

  adicionarItem(etapa: Etapa): void {
    const texto = (this.novoItemTexto[etapa.id] ?? '').trim();
    if (!texto) {
      return;
    }
    this.checklistItemService.adicionar(etapa.id, { descricao: texto }).subscribe(() => {
      this.novoItemTexto[etapa.id] = '';
      this.carregar();
    });
  }

  removerItem(itemId: number): void {
    this.checklistItemService.excluir(itemId).subscribe(() => this.carregar());
  }

  adicionarEtapa(): void {
    const nome = this.novaEtapaNome().trim();
    if (!nome) {
      return;
    }
    this.etapaService.adicionar(this.municipioId, { nome }).subscribe(() => {
      this.novaEtapaNome.set('');
      this.mostrarFormNovaEtapa.set(false);
      this.carregar();
    });
  }

  removerEtapa(etapa: Etapa): void {
    if (!confirm(`Remover a etapa "${etapa.nome}" e todos os seus itens?`)) {
      return;
    }
    this.etapaService.excluir(etapa.id).subscribe(() => this.carregar());
  }

  iniciarEdicaoEtapa(etapa: Etapa): void {
    this.etapaEmEdicaoId.set(etapa.id);
    this.etapaEdicaoNome.set(etapa.nome);
  }

  salvarEdicaoEtapa(etapa: Etapa): void {
    const nome = this.etapaEdicaoNome().trim();
    if (!nome) {
      return;
    }
    this.etapaService.atualizar(etapa.id, { nome, descricao: etapa.descricao ?? undefined }).subscribe(() => {
      this.etapaEmEdicaoId.set(null);
      this.carregar();
    });
  }

  cancelarEdicaoEtapa(): void {
    this.etapaEmEdicaoId.set(null);
  }

  moverEtapa(etapa: Etapa, direcao: 'cima' | 'baixo'): void {
    const etapas = this.municipio()?.etapas ?? [];
    const index = etapas.findIndex((e) => e.id === etapa.id);
    const alvoIndex = direcao === 'cima' ? index - 1 : index + 1;
    if (index === -1 || alvoIndex < 0 || alvoIndex >= etapas.length) {
      return;
    }
    const atual = etapas[index];
    const vizinha = etapas[alvoIndex];
    forkJoin([
      this.etapaService.atualizar(atual.id, this.construirRequestEtapa(atual, { ordem: vizinha.ordem })),
      this.etapaService.atualizar(vizinha.id, this.construirRequestEtapa(vizinha, { ordem: atual.ordem }))
    ]).subscribe(() => this.carregar());
  }

  atualizarDataSolicitacao(etapa: Etapa, valor: string): void {
    this.salvarCronograma(etapa, { dataSolicitacao: valor || undefined });
  }

  atualizarDataInicio(etapa: Etapa, valor: string): void {
    this.salvarCronograma(etapa, { dataInicio: valor || undefined });
  }

  atualizarDataFim(etapa: Etapa, valor: string): void {
    this.salvarCronograma(etapa, { dataFim: valor || undefined });
  }

  atualizarDuracao(etapa: Etapa, valor: number | null): void {
    this.salvarCronograma(etapa, { duracaoDias: valor ?? undefined });
  }

  atualizarPercentualPrevisto(etapa: Etapa, valor: number | null): void {
    this.salvarCronograma(etapa, { percentualPrevisto: valor ?? undefined });
  }

  atualizarPredecessoras(etapa: Etapa, valor: string): void {
    this.salvarCronograma(etapa, { predecessoras: valor || undefined });
  }

  private salvarCronograma(etapa: Etapa, patch: Partial<EtapaRequest>): void {
    this.etapaService.atualizar(etapa.id, this.construirRequestEtapa(etapa, patch)).subscribe(() => this.carregar());
  }

  private construirRequestEtapa(etapa: Etapa, patch: Partial<EtapaRequest>): EtapaRequest {
    return {
      nome: etapa.nome,
      descricao: etapa.descricao ?? undefined,
      dataSolicitacao: etapa.dataSolicitacao ?? undefined,
      dataInicio: etapa.dataInicio ?? undefined,
      dataFim: etapa.dataFim ?? undefined,
      duracaoDias: etapa.duracaoDias ?? undefined,
      percentualPrevisto: etapa.percentualPrevisto ?? undefined,
      predecessoras: etapa.predecessoras ?? undefined,
      ...patch
    };
  }

  get etapasConcluidas(): Etapa[] {
    return this.municipio()?.etapas.filter((etapa) => etapa.status === 'CONCLUIDA') ?? [];
  }

  get resumoTexto(): string {
    const m = this.municipio();
    if (!m) {
      return '';
    }
    const concluidas = this.etapasConcluidas;
    if (concluidas.length === 0) {
      return `Resumo de etapas concluídas — ${m.nome}\nNenhuma etapa concluída até o momento.`;
    }
    const linhas = [`Resumo de etapas concluídas — ${m.nome}`, `Gerado em ${new Date().toLocaleDateString('pt-BR')}`, ''];
    for (const etapa of concluidas) {
      linhas.push(`✅ ${etapa.nome}`);
      if (etapa.descricao) {
        linhas.push(`   ${etapa.descricao}`);
      }
      for (const item of etapa.checklistItems) {
        linhas.push(`   - ${item.descricao}`);
      }
      linhas.push('');
    }
    return linhas.join('\n').trim();
  }

  abrirResumo(): void {
    this.resumoCopiado.set(false);
    this.mostrarResumo.set(true);
  }

  fecharResumo(): void {
    this.mostrarResumo.set(false);
  }

  copiarResumo(): void {
    navigator.clipboard.writeText(this.resumoTexto).then(() => {
      this.resumoCopiado.set(true);
      setTimeout(() => this.resumoCopiado.set(false), 2000);
    });
  }
}
