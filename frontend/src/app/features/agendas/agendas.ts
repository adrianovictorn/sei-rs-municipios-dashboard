import { Component, computed, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AgendaService } from '../../core/services/agenda.service';
import { MunicipioService } from '../../core/services/municipio.service';
import { TipoAgendaService } from '../../core/services/tipo-agenda.service';
import { Agenda, AgendaRequest } from '../../core/models/agenda.model';
import { MunicipioSummary } from '../../core/models/municipio.model';
import { TipoAgenda } from '../../core/models/tipo-agenda.model';

@Component({
  selector: 'app-agendas',
  standalone: true,
  imports: [RouterLink, FormsModule, DatePipe],
  templateUrl: './agendas.html'
})
export class Agendas {
  private readonly agendaService = inject(AgendaService);
  private readonly municipioService = inject(MunicipioService);
  private readonly tipoAgendaService = inject(TipoAgendaService);

  agendas = signal<Agenda[]>([]);
  municipios = signal<MunicipioSummary[]>([]);
  tipos = signal<TipoAgenda[]>([]);
  carregando = signal(true);
  erro = signal<string | null>(null);
  agoraIso = new Date().toISOString();

  filtroMunicipioId = signal<number | null>(null);
  filtroTipoId = signal<number | null>(null);
  somentePendentes = signal(false);

  mostrarGerenciarTipos = signal(false);
  novoTipoNome = signal('');
  tipoEmEdicaoId = signal<number | null>(null);
  tipoEdicaoNome = signal('');

  mostrarForm = signal(false);
  agendaEmEdicaoId = signal<number | null>(null);
  formMunicipioId = signal<number | null>(null);
  formTipoId = signal<number | null>(null);
  formTitulo = signal('');
  formDataHora = signal('');
  formLocal = signal('');
  formObservacoes = signal('');

  linhas = computed(() => {
    let lista = this.agendas();
    if (this.filtroMunicipioId() !== null) {
      lista = lista.filter((a) => a.municipioId === this.filtroMunicipioId());
    }
    if (this.filtroTipoId() !== null) {
      lista = lista.filter((a) => a.tipoAgendaId === this.filtroTipoId());
    }
    if (this.somentePendentes()) {
      lista = lista.filter((a) => !a.realizada);
    }
    return [...lista].sort((a, b) => a.dataHora.localeCompare(b.dataHora));
  });

  constructor() {
    this.carregar();
  }

  carregar(): void {
    this.carregando.set(true);
    this.agendaService.listar().subscribe({
      next: (agendas) => {
        this.agendas.set(agendas);
        this.carregando.set(false);
      },
      error: () => {
        this.erro.set('Não foi possível carregar as agendas.');
        this.carregando.set(false);
      }
    });
    this.municipioService.listar().subscribe((municipios) => this.municipios.set(municipios));
    this.tipoAgendaService.listar().subscribe((tipos) => this.tipos.set(tipos));
  }

  atrasada(agenda: Agenda): boolean {
    return !agenda.realizada && agenda.dataHora < this.agoraIso;
  }

  alternarRealizada(agenda: Agenda): void {
    this.agendaService.atualizar(agenda.id, {
      titulo: agenda.titulo,
      dataHora: agenda.dataHora,
      local: agenda.local ?? undefined,
      observacoes: agenda.observacoes ?? undefined,
      tipoAgendaId: agenda.tipoAgendaId ?? undefined,
      realizada: !agenda.realizada
    }).subscribe(() => this.carregar());
  }

  removerAgenda(agenda: Agenda): void {
    if (!confirm(`Remover a agenda "${agenda.titulo}"?`)) {
      return;
    }
    this.agendaService.excluir(agenda.id).subscribe(() => this.carregar());
  }

  abrirNovaAgenda(): void {
    this.agendaEmEdicaoId.set(null);
    this.formMunicipioId.set(this.municipios()[0]?.id ?? null);
    this.formTipoId.set(null);
    this.formTitulo.set('');
    this.formDataHora.set('');
    this.formLocal.set('');
    this.formObservacoes.set('');
    this.mostrarForm.set(true);
  }

  editarAgenda(agenda: Agenda): void {
    this.agendaEmEdicaoId.set(agenda.id);
    this.formMunicipioId.set(agenda.municipioId);
    this.formTipoId.set(agenda.tipoAgendaId);
    this.formTitulo.set(agenda.titulo);
    this.formDataHora.set(agenda.dataHora.slice(0, 16));
    this.formLocal.set(agenda.local ?? '');
    this.formObservacoes.set(agenda.observacoes ?? '');
    this.mostrarForm.set(true);
  }

  cancelarForm(): void {
    this.mostrarForm.set(false);
  }

  salvarForm(): void {
    const titulo = this.formTitulo().trim();
    const municipioId = this.formMunicipioId();
    const dataHora = this.formDataHora();
    if (!titulo || !municipioId || !dataHora) {
      return;
    }

    const request: AgendaRequest = {
      titulo,
      dataHora,
      local: this.formLocal().trim() || undefined,
      observacoes: this.formObservacoes().trim() || undefined,
      tipoAgendaId: this.formTipoId() ?? undefined
    };

    const idEmEdicao = this.agendaEmEdicaoId();
    const request$ = idEmEdicao
      ? this.agendaService.atualizar(idEmEdicao, request)
      : this.agendaService.criar(municipioId, request);

    request$.subscribe(() => {
      this.mostrarForm.set(false);
      this.carregar();
    });
  }

  adicionarTipo(): void {
    const nome = this.novoTipoNome().trim();
    if (!nome) {
      return;
    }
    this.tipoAgendaService.criar({ nome }).subscribe(() => {
      this.novoTipoNome.set('');
      this.carregar();
    });
  }

  iniciarEdicaoTipo(tipo: TipoAgenda): void {
    this.tipoEmEdicaoId.set(tipo.id);
    this.tipoEdicaoNome.set(tipo.nome);
  }

  salvarEdicaoTipo(tipo: TipoAgenda): void {
    const nome = this.tipoEdicaoNome().trim();
    if (!nome) {
      return;
    }
    this.tipoAgendaService.atualizar(tipo.id, { nome }).subscribe(() => {
      this.tipoEmEdicaoId.set(null);
      this.carregar();
    });
  }

  cancelarEdicaoTipo(): void {
    this.tipoEmEdicaoId.set(null);
  }

  removerTipo(tipo: TipoAgenda): void {
    if (!confirm(`Remover o tipo de agenda "${tipo.nome}"? Agendas que usam esse tipo ficam sem tipo.`)) {
      return;
    }
    this.tipoAgendaService.excluir(tipo.id).subscribe(() => this.carregar());
  }
}
