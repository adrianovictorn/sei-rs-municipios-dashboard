import { Component, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { MunicipioService } from '../../../core/services/municipio.service';
import { MunicipioSummary } from '../../../core/models/municipio.model';
import { ProgressBar } from '../../../shared/components/progress-bar/progress-bar';

@Component({
  selector: 'app-municipio-list',
  standalone: true,
  imports: [RouterLink, ProgressBar],
  templateUrl: './municipio-list.html'
})
export class MunicipioList {
  private readonly municipioService = inject(MunicipioService);

  municipios = signal<MunicipioSummary[]>([]);
  carregando = signal(true);
  erro = signal<string | null>(null);

  constructor() {
    this.carregar();
  }

  carregar(): void {
    this.carregando.set(true);
    this.municipioService.listar().subscribe({
      next: (municipios) => {
        this.municipios.set(municipios);
        this.carregando.set(false);
      },
      error: () => {
        this.erro.set('Não foi possível carregar os municípios.');
        this.carregando.set(false);
      }
    });
  }

  excluir(municipio: MunicipioSummary): void {
    if (!confirm(`Remover o município "${municipio.nome}"? Essa ação não pode ser desfeita.`)) {
      return;
    }
    this.municipioService.excluir(municipio.id).subscribe(() => this.carregar());
  }
}
