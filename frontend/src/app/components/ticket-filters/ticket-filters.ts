import { Component, output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { FiltrosChamado } from '../../core/models';
import { CATEGORIA_OPCOES, PRIORIDADE_OPCOES, STATUS_OPCOES } from '../../shared/constants';

@Component({
  imports: [FormsModule, MatIconModule],
  selector: 'app-ticket-filters',
  styleUrl: './ticket-filters.css',
  templateUrl: './ticket-filters.html',
})
export class TicketFilters {
  readonly filtrar = output<FiltrosChamado>();
  readonly limpar = output<void>();

  readonly statusOpcoes = STATUS_OPCOES;
  readonly prioridadeOpcoes = PRIORIDADE_OPCOES;
  readonly categoriaOpcoes = CATEGORIA_OPCOES;

  filtros: FiltrosChamado = {
    status: '',
    prioridade: '',
    categoria: '',
    busca: '',
  };

  aplicar(): void {
    this.filtrar.emit({ ...this.filtros });
  }

  limparFiltros(): void {
    this.filtros = { status: '', prioridade: '', categoria: '', busca: '' };
    this.limpar.emit();
  }
}
