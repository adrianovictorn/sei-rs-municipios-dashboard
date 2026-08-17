import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { EquipeService } from '../../core/services/equipe.service';
import { Equipe } from '../../core/models/equipe.model';

@Component({
  selector: 'app-equipes',
  standalone: true,
  imports: [RouterLink, FormsModule],
  templateUrl: './equipes.html'
})
export class Equipes {
  private readonly equipeService = inject(EquipeService);

  equipes = signal<Equipe[]>([]);
  carregando = signal(true);
  erro = signal<string | null>(null);

  novoNome = signal('');

  equipeEmEdicaoId = signal<number | null>(null);
  edicaoNome = signal('');

  constructor() {
    this.carregar();
  }

  carregar(): void {
    this.carregando.set(true);
    this.equipeService.listar().subscribe({
      next: (equipes) => {
        this.equipes.set(equipes);
        this.carregando.set(false);
      },
      error: () => {
        this.erro.set('Não foi possível carregar as equipes.');
        this.carregando.set(false);
      }
    });
  }

  adicionar(): void {
    const nome = this.novoNome().trim();
    if (!nome) {
      return;
    }
    this.equipeService.criar({ nome }).subscribe(() => {
      this.novoNome.set('');
      this.carregar();
    });
  }

  iniciarEdicao(equipe: Equipe): void {
    this.equipeEmEdicaoId.set(equipe.id);
    this.edicaoNome.set(equipe.nome);
  }

  salvarEdicao(equipe: Equipe): void {
    const nome = this.edicaoNome().trim();
    if (!nome) {
      return;
    }
    this.equipeService.atualizar(equipe.id, { nome }).subscribe(() => {
      this.equipeEmEdicaoId.set(null);
      this.carregar();
    });
  }

  cancelarEdicao(): void {
    this.equipeEmEdicaoId.set(null);
  }

  remover(equipe: Equipe): void {
    if (!confirm(`Remover a equipe "${equipe.nome}"? Municípios vinculados a ela ficam sem equipe.`)) {
      return;
    }
    this.equipeService.excluir(equipe.id).subscribe(() => this.carregar());
  }
}
