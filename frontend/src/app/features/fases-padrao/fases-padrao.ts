import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
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
  novaFaseExibirMatriz = signal(false);
  novaFaseOrdemMatriz = signal<number | null>(null);

  faseEmEdicaoId = signal<number | null>(null);
  faseEdicaoNome = signal('');
  faseEdicaoDescricao = signal('');
  faseEdicaoDuracao = signal<number | null>(null);
  faseEdicaoExibirMatriz = signal(false);
  faseEdicaoOrdemMatriz = signal<number | null>(null);

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
        duracaoDias: this.novaFaseDuracao() ?? undefined,
        exibirMatriz: this.novaFaseExibirMatriz(),
        ordemMatriz: this.novaFaseOrdemMatriz() ?? undefined
      })
      .subscribe(() => {
        this.novaFaseNome.set('');
        this.novaFaseDescricao.set('');
        this.novaFaseDuracao.set(null);
        this.novaFaseExibirMatriz.set(false);
        this.novaFaseOrdemMatriz.set(null);
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

  removerFaseEmTodosMunicipios(fase: EtapaTemplate): void {
    if (!confirm(`Apagar a fase "${fase.nome}" e TODO o progresso já registrado nela em TODOS os municípios? Essa ação não pode ser desfeita.`)) {
      return;
    }
    this.etapaTemplateService.excluirFase(fase.id, true).subscribe(() => this.carregar());
  }

  iniciarEdicaoFase(fase: EtapaTemplate): void {
    this.faseEmEdicaoId.set(fase.id);
    this.faseEdicaoNome.set(fase.nome);
    this.faseEdicaoDescricao.set(fase.descricao ?? '');
    this.faseEdicaoDuracao.set(fase.duracaoDias);
    this.faseEdicaoExibirMatriz.set(fase.exibirMatriz);
    this.faseEdicaoOrdemMatriz.set(fase.ordemMatriz);
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
        duracaoDias: this.faseEdicaoDuracao() ?? undefined,
        exibirMatriz: this.faseEdicaoExibirMatriz(),
        ordemMatriz: this.faseEdicaoOrdemMatriz() ?? undefined
      })
      .subscribe(() => {
        this.faseEmEdicaoId.set(null);
        this.carregar();
      });
  }

  cancelarEdicaoFase(): void {
    this.faseEmEdicaoId.set(null);
  }

  moverFase(fase: EtapaTemplate, direcao: 'cima' | 'baixo'): void {
    const templates = this.templates();
    const index = templates.findIndex((t) => t.id === fase.id);
    const alvoIndex = direcao === 'cima' ? index - 1 : index + 1;
    if (index === -1 || alvoIndex < 0 || alvoIndex >= templates.length) {
      return;
    }
    const atual = templates[index];
    const vizinha = templates[alvoIndex];
    const novaOrdemAtual = vizinha.ordem !== atual.ordem ? vizinha.ordem : vizinha.ordem - 1;
    const novaOrdemVizinha = vizinha.ordem !== atual.ordem ? atual.ordem : vizinha.ordem + 1;
    forkJoin([
      this.etapaTemplateService.atualizarFase(atual.id, this.construirRequestFase(atual, novaOrdemAtual)),
      this.etapaTemplateService.atualizarFase(vizinha.id, this.construirRequestFase(vizinha, novaOrdemVizinha))
    ]).subscribe({
      next: () => this.carregar(),
      error: (err) => alert(err?.error?.message ?? 'Não foi possível reordenar essa fase. Veja o console para detalhes.')
    });
  }

  private construirRequestFase(fase: EtapaTemplate, ordem: number) {
    return {
      nome: fase.nome,
      descricao: fase.descricao ?? undefined,
      ordem,
      duracaoDias: fase.duracaoDias ?? undefined,
      exibirMatriz: fase.exibirMatriz,
      ordemMatriz: fase.ordemMatriz ?? undefined
    };
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

  removerTarefaEmTodosMunicipios(tarefa: ChecklistItemTemplate): void {
    if (!confirm(`Apagar a tarefa "${tarefa.descricao}" e TODO o progresso já registrado nela em TODOS os municípios? Essa ação não pode ser desfeita.`)) {
      return;
    }
    this.etapaTemplateService.excluirTarefa(tarefa.id, true).subscribe(() => this.carregar());
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

  moverTarefa(fase: EtapaTemplate, tarefa: ChecklistItemTemplate, direcao: 'cima' | 'baixo'): void {
    const itens = fase.itens;
    const index = itens.findIndex((t) => t.id === tarefa.id);
    const alvoIndex = direcao === 'cima' ? index - 1 : index + 1;
    if (index === -1 || alvoIndex < 0 || alvoIndex >= itens.length) {
      return;
    }
    const atual = itens[index];
    const vizinha = itens[alvoIndex];
    const novaOrdemAtual = vizinha.ordem !== atual.ordem ? vizinha.ordem : vizinha.ordem - 1;
    const novaOrdemVizinha = vizinha.ordem !== atual.ordem ? atual.ordem : vizinha.ordem + 1;
    forkJoin([
      this.etapaTemplateService.atualizarTarefa(atual.id, { descricao: atual.descricao, ordem: novaOrdemAtual, duracaoDias: atual.duracaoDias ?? undefined }),
      this.etapaTemplateService.atualizarTarefa(vizinha.id, { descricao: vizinha.descricao, ordem: novaOrdemVizinha, duracaoDias: vizinha.duracaoDias ?? undefined })
    ]).subscribe({
      next: () => this.carregar(),
      error: (err) => alert(err?.error?.message ?? 'Não foi possível reordenar essa tarefa. Veja o console para detalhes.')
    });
  }
}
