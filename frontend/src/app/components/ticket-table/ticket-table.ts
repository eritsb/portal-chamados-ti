import { Component, computed, input, output } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Chamado } from '../../core/models';
import { CATEGORIA_ROTULO, formatarData } from '../../shared/constants';
import { PriorityBadge } from '../priority-badge/priority-badge';
import { StatusBadge } from '../status-badge/status-badge';

export type TicketTableMode = 'gestao' | 'solicitante' | 'recentes';

@Component({
  imports: [PriorityBadge, RouterLink, StatusBadge],
  selector: 'app-ticket-table',
  styleUrl: './ticket-table.css',
  templateUrl: './ticket-table.html',
})
export class TicketTable {
  readonly chamados = input.required<Chamado[]>();
  readonly modo = input<TicketTableMode>('gestao');
  readonly podeExcluir = input(false);
  readonly mensagemVazia = input('Nenhum chamado encontrado.');
  readonly vazioLink = input<string | null>(null);
  readonly vazioLinkRotulo = input('Abrir chamado');

  readonly excluir = output<Chamado>();

  readonly linkDetalhe = computed(() =>
    this.modo() === 'solicitante' ? '/meus-chamados' : '/chamados',
  );
  readonly colspan = computed(() => (this.modo() === 'gestao' ? 8 : 6));
  readonly categoriaRotulo = CATEGORIA_ROTULO;
  readonly formatarData = formatarData;
}
