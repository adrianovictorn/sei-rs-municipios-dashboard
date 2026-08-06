import { Component, computed, input } from '@angular/core';
import { EtapaStatus } from '../../../core/models/etapa.model';

const LABELS: Record<EtapaStatus, string> = {
  NAO_INICIADA: 'Não iniciada',
  EM_ANDAMENTO: 'Em andamento',
  CONCLUIDA: 'Concluída'
};

const CLASSES: Record<EtapaStatus, string> = {
  NAO_INICIADA: 'bg-gray-100 text-gray-700',
  EM_ANDAMENTO: 'bg-brand-yellow/15 text-brand-yellow',
  CONCLUIDA: 'bg-brand-green/15 text-brand-green'
};

@Component({
  selector: 'app-status-badge',
  standalone: true,
  template: `
    <span class="inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-semibold" [class]="cssClass()">
      {{ label() }}
    </span>
  `
})
export class StatusBadge {
  status = input.required<EtapaStatus>();

  label = computed(() => LABELS[this.status()]);
  cssClass = computed(() => CLASSES[this.status()]);
}
