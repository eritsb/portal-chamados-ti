import { Component, computed, input } from '@angular/core';
import { PrioridadeChamado } from '../../core/models';
import { PRIORIDADE_ROTULO, prioridadeClass } from '../../shared/constants';

@Component({
  selector: 'app-priority-badge',
  styleUrl: './priority-badge.css',
  templateUrl: './priority-badge.html',
})
export class PriorityBadge {
  readonly prioridade = input.required<PrioridadeChamado>();
  readonly rotulo = computed(() => PRIORIDADE_ROTULO[this.prioridade()]);
  readonly classe = computed(() => prioridadeClass(this.prioridade()));
}
