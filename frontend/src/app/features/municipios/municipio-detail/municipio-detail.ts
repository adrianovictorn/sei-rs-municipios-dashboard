import { Component, inject, signal } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { MunicipioService } from '../../../core/services/municipio.service';
import { EtapaService } from '../../../core/services/etapa.service';
import { ChecklistItemService } from '../../../core/services/checklist-item.service';
import { MunicipioDetail } from '../../../core/models/municipio.model';
import { Etapa } from '../../../core/models/etapa.model';
import { ProgressBar } from '../../../shared/components/progress-bar/progress-bar';
import { Roadmap } from '../../../shared/components/roadmap/roadmap';
import { StatusBadge } from '../../../shared/components/status-badge/status-badge';

@Component({
  selector: 'app-municipio-detail',
  standalone: true,
  imports: [RouterLink, FormsModule, DecimalPipe, ProgressBar, Roadmap, StatusBadge],
  templateUrl: './municipio-detail.html'
})
export class MunicipioDetailPage {
  private readonly route = inject(ActivatedRoute);
  private readonly municipioService = inject(MunicipioService);
  private readonly etapaService = inject(EtapaService);
  private readonly checklistItemService = inject(ChecklistItemService);

  municipioId = Number(this.route.snapshot.paramMap.get('id'));

  municipio = signal<MunicipioDetail | null>(null);
  carregando = signal(true);
  expandedEtapaId = signal<number | null>(null);

  mostrarFormNovaEtapa = signal(false);
  novaEtapaNome = signal('');

  etapaEmEdicaoId = signal<number | null>(null);
  etapaEdicaoNome = signal('');

  novoItemTexto: Record<number, string> = {};

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
}
