import { Component, effect, input, output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { AtualizarStatusRequest, Chamado, Usuario } from '../../core/models';
import { STATUS_OPCOES } from '../../shared/constants';

@Component({
  imports: [FormsModule, MatIconModule],
  selector: 'app-ticket-status-form',
  styleUrl: './ticket-status-form.css',
  templateUrl: './ticket-status-form.html',
})
export class TicketStatusForm {
  readonly chamado = input.required<Chamado>();
  readonly usuarios = input<Usuario[]>([]);
  readonly salvando = input(false);
  readonly salvar = output<AtualizarStatusRequest>();

  readonly statusOpcoes = STATUS_OPCOES;

  formStatus: AtualizarStatusRequest = {
    status: 'ABERTO',
    tecnicoId: null,
    solucao: null,
  };

  constructor() {
    effect(() => {
      const chamado = this.chamado();
      this.formStatus = {
        status: chamado.status,
        tecnicoId: chamado.tecnicoId,
        solucao: chamado.solucao,
      };
    });
  }

  enviar(): void {
    this.salvar.emit({
      status: this.formStatus.status,
      tecnicoId: this.formStatus.tecnicoId ? Number(this.formStatus.tecnicoId) : null,
      solucao: this.formStatus.solucao?.trim() ? this.formStatus.solucao.trim() : null,
    });
  }
}
