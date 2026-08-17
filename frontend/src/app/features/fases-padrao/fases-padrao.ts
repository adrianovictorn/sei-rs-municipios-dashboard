import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { EtapaTemplateService } from '../../core/services/etapa-template.service';
import { ChecklistItemTemplate, EtapaTemplate } from '../../core/models/etapa-template.model';

@Component({
  selector: 'app-fases-padrao',
  standalone: true,
  imports: [RouterLink, FormsModule],
  templateUrl: './fases-padrao.html'
})
export class FasesPadrao {
  private readonly etapaTemplateService = inject(EtapaTemplateService);

  templates = signal<EtapaTemplate[]>([]);
  carregando = signal(true);
  erro = signal<string | null>(null);
  expandedFaseId = signal<number | null>(null);

  mostrarFormNovaFase = signal(false);
  novaFaseNome = signal('');
  novaFaseDescricao = signal('');
  novaFaseDuracao = signal<number | null>(null);

  faseEmEdicaoId = signal<number | null>(null);
  faseEdicaoNome = signal('');
  faseEdicaoDescricao = signal('');
  faseEdicaoDuracao = signal<number | null>(null);

  novaTarefaTexto: Record<number, string> = {};
  novaTarefaDuracao: Record<number, number | null> = {};

  tarefaEmEdicaoId = signal<number | null>(null);
  tarefaEdicaoDescricao = signal('');
  tarefaEdicaoDuracao = signal<number | null>(null);

  constructor() {
    this.carregar();
  }

  carregar(): void {
    this.carregando.set(true);
    this.etapaTemplateService.listar().subscribe({
      next: (templates) => {
        this.templates.set(templates);
        this.carregando.set(false);
        if (this.expandedFaseId() === null && templates.length > 0) {
          this.expandedFaseId.set(templates[0].id);
        }
      },
      error: () => {
        this.erro.set('Não foi possível carregar as fases padrão.');
        this.carregando.set(false);
      }
    });
  }

  toggleFase(id: number): void {
    this.expandedFaseId.set(this.expandedFaseId() === id ? null : id);
  }

  adicionarFase(): void {
    const nome = this.novaFaseNome().trim();
    if (!nome) {
      return;
    }
    this.etapaTemplateService
      .adicionarFase({
        nome,
        descricao: this.novaFaseDescricao().trim() || undefined,
        duracaoDias: this.novaFaseDuracao() ?? undefined
      })
      .subscribe(() => {
        this.novaFaseNome.set('');
        this.novaFaseDescricao.set('');
        this.novaFaseDuracao.set(null);
        this.mostrarFormNovaFase.set(false);
        this.carregar();
      });
  }

  removerFase(fase: EtapaTemplate): void {
    if (!confirm(`Remover a fase padrão "${fase.nome}"? Ela deixará de existir como padrão nos municípios que a utilizam (o texto atual é preservado localmente em cada um).`)) {
      return;
    }
    this.etapaTemplateService.excluirFase(fase.id).subscribe(() => this.carregar());
  }

  iniciarEdicaoFase(fase: EtapaTemplate): void {
    this.faseEmEdicaoId.set(fase.id);
    this.faseEdicaoNome.set(fase.nome);
    this.faseEdicaoDescricao.set(fase.descricao ?? '');
    this.faseEdicaoDuracao.set(fase.duracaoDias);
  }

  salvarEdicaoFase(fase: EtapaTemplate): void {
    const nome = this.faseEdicaoNome().trim();
    if (!nome) {
      return;
    }
    this.etapaTemplateService
      .atualizarFase(fase.id, {
        nome,
        descricao: this.faseEdicaoDescricao().trim() || undefined,
        ordem: fase.ordem,
        duracaoDias: this.faseEdicaoDuracao() ?? undefined
      })
      .subscribe(() => {
        this.faseEmEdicaoId.set(null);
        this.carregar();
      });
  }

  cancelarEdicaoFase(): void {
    this.faseEmEdicaoId.set(null);
  }

  adicionarTarefa(fase: EtapaTemplate): void {
    const descricao = (this.novaTarefaTexto[fase.id] ?? '').trim();
    if (!descricao) {
      return;
    }
    this.etapaTemplateService
      .adicionarTarefa(fase.id, {
        descricao,
        duracaoDias: this.novaTarefaDuracao[fase.id] ?? undefined
      })
      .subscribe(() => {
        this.novaTarefaTexto[fase.id] = '';
        this.novaTarefaDuracao[fase.id] = null;
        this.carregar();
      });
  }

  removerTarefa(tarefa: ChecklistItemTemplate): void {
    if (!confirm(`Remover a tarefa padrão "${tarefa.descricao}"?`)) {
      return;
    }
    this.etapaTemplateService.excluirTarefa(tarefa.id).subscribe(() => this.carregar());
  }

  iniciarEdicaoTarefa(tarefa: ChecklistItemTemplate): void {
    this.tarefaEmEdicaoId.set(tarefa.id);
    this.tarefaEdicaoDescricao.set(tarefa.descricao);
    this.tarefaEdicaoDuracao.set(tarefa.duracaoDias);
  }

  salvarEdicaoTarefa(tarefa: ChecklistItemTemplate): void {
    const descricao = this.tarefaEdicaoDescricao().trim();
    if (!descricao) {
      return;
    }
    this.etapaTemplateService
      .atualizarTarefa(tarefa.id, {
        descricao,
        ordem: tarefa.ordem,
        duracaoDias: this.tarefaEdicaoDuracao() ?? undefined
      })
      .subscribe(() => {
        this.tarefaEmEdicaoId.set(null);
        this.carregar();
      });
  }

  cancelarEdicaoTarefa(): void {
    this.tarefaEmEdicaoId.set(null);
  }
}
