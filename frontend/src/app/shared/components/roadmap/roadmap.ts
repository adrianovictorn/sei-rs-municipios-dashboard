import { Component, input, output } from '@angular/core';
import { Etapa } from '../../../core/models/etapa.model';

@Component({
  selector: 'app-roadmap',
  standalone: true,
  templateUrl: './roadmap.html'
})
export class Roadmap {
  etapas = input.required<Etapa[]>();
  selectedEtapaId = input<number | null>(null);
  etapaSelecionada = output<number>();

  statusClass(etapa: Etapa): string {
    switch (etapa.status) {
      case 'CONCLUIDA':
        return 'bg-brand-green border-brand-green text-white';
      case 'EM_ANDAMENTO':
        return 'bg-brand-yellow border-brand-yellow text-white';
      default:
        return 'bg-white border-gray-300 text-gray-500';
    }
  }

  connectorClass(etapa: Etapa): string {
    return etapa.status === 'CONCLUIDA' ? 'bg-brand-green' : 'bg-gray-300';
  }
}
