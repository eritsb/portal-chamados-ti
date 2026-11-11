import { Component, computed, input } from '@angular/core';
import { StatusChamado } from '../../core/models';
import { STATUS_ROTULO, statusClass } from '../../shared/constants';

@Component({
  selector: 'app-status-badge',
  styleUrl: './status-badge.css',
  templateUrl: './status-badge.html',
})
export class StatusBadge {
  readonly status = input.required<StatusChamado>();
  readonly rotulo = computed(() => STATUS_ROTULO[this.status()]);
  readonly classe = computed(() => statusClass(this.status()));
}
