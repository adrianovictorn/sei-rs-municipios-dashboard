import { Component, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { MunicipioService } from '../../../core/services/municipio.service';
import { ChecklistItemService } from '../../../core/services/checklist-item.service';
import { MunicipioDetail } from '../../../core/models/municipio.model';
import { Etapa } from '../../../core/models/etapa.model';
import { ChecklistItem } from '../../../core/models/checklist-item.model';

interface AtividadeCard {
  itemId: number;
  descricao: string;
  concluido: boolean;
  etapaId: number;
  etapaNome: string;
}

const CELL_COLOR_CONCLUIDA = 'bg-brand-green text-white';
const CELL_COLOR_EM_ANDAMENTO = 'bg-brand-yellow text-white';
const CELL_COLOR_NAO_INICIADA = 'bg-brand-red text-white';

@Component({
  selector: 'app-atividades-kanban',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './atividades-kanban.html'
})
export class AtividadesKanban {
  private readonly municipioService = inject(MunicipioService);
  private readonly checklistItemService = inject(ChecklistItemService);

  municipiosDetalhados = signal<MunicipioDetail[]>([]);
  municipioSelecionadoId = signal<number | null>(null);

  carregando = signal(true);

  constructor() {
    this.carregarTudo();
  }

  private carregarTudo(): void {
    this.municipioService.listar().subscribe((resumos) => {
      if (resumos.length === 0) {
        this.municipiosDetalhados.set([]);
        this.carregando.set(false);
        return;
      }
      forkJoin(resumos.map((resumo) => this.municipioService.obter(resumo.id))).subscribe((detalhados) => {
        this.municipiosDetalhados.set(detalhados);
        this.carregando.set(false);
        if (this.municipioSelecionadoId() === null) {
          this.municipioSelecionadoId.set(detalhados[0].id);
        }
      });
    });
  }

  private refrescarMunicipio(id: number): void {
    this.municipioService.obter(id).subscribe((atualizado) => {
      this.municipiosDetalhados.set(
        this.municipiosDetalhados().map((m) => (m.id === id ? atualizado : m))
      );
    });
  }

  municipioSelecionado = computed<MunicipioDetail | null>(
    () => this.municipiosDetalhados().find((m) => m.id === this.municipioSelecionadoId()) ?? null
  );

  atividades = computed<AtividadeCard[]>(() => {
    const m = this.municipioSelecionado();
    if (!m) {
      return [];
    }
    return m.etapas.flatMap((etapa) =>
      etapa.checklistItems.map((item) => ({
        itemId: item.id,
        descricao: item.descricao,
        concluido: item.concluido,
        etapaId: etapa.id,
        etapaNome: etapa.nome
      }))
    );
  });

  pendentes = computed(() => this.atividades().filter((a) => !a.concluido));
  concluidas = computed(() => this.atividades().filter((a) => a.concluido));

  etapasOrdenadas(municipio: MunicipioDetail): Etapa[] {
    return [...municipio.etapas].sort((a, b) => a.ordem - b.ordem);
  }

  itensOrdenados(etapa: Etapa): ChecklistItem[] {
    return [...etapa.checklistItems].sort((a, b) => a.ordem - b.ordem);
  }

  corItem(item: ChecklistItem, etapa: Etapa): string {
    if (item.concluido) {
      return CELL_COLOR_CONCLUIDA;
    }
    return etapa.status === 'EM_ANDAMENTO' ? CELL_COLOR_EM_ANDAMENTO : CELL_COLOR_NAO_INICIADA;
  }

  tituloItem(item: ChecklistItem): string {
    return `${item.descricao} — ${item.concluido ? 'concluída' : 'pendente'}`;
  }

  selecionarMunicipio(id: number): void {
    this.municipioSelecionadoId.set(id);
  }

  moverAtividade(atividade: AtividadeCard): void {
    this.checklistItemService.atualizar(atividade.itemId, { concluido: !atividade.concluido }).subscribe(() => {
      const id = this.municipioSelecionadoId();
      if (id !== null) {
        this.refrescarMunicipio(id);
      }
    });
  }
}
