import { Component, input } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { Chamado } from '../../core/models';
import { formatarData, iniciais } from '../../shared/constants';

@Component({
  imports: [MatIconModule],
  selector: 'app-ticket-timeline',
  styleUrl: './ticket-timeline.css',
  templateUrl: './ticket-timeline.html',
})
export class TicketTimeline {
  readonly chamado = input.required<Chamado>();
  readonly formatarData = formatarData;
  readonly iniciais = iniciais;
}
