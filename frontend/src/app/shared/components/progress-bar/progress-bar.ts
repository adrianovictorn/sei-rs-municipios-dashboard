import { Component, computed, input } from '@angular/core';

@Component({
  selector: 'app-progress-bar',
  standalone: true,
  templateUrl: './progress-bar.html'
})
export class ProgressBar {
  value = input<number>(0);
  showLabel = input<boolean>(true);

  clampedValue = computed(() => Math.min(100, Math.max(0, this.value())));

  colorClass = computed(() => {
    const v = this.clampedValue();
    if (v >= 66) return 'bg-brand-green';
    if (v >= 33) return 'bg-brand-yellow';
    return 'bg-brand-red';
  });
}
