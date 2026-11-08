import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { RouterLink } from '@angular/router';
import { ApiService } from '../../core/api.service';
import { AuthService } from '../../core/auth.service';
import { Chamado, FiltrosChamado } from '../../core/models';
import {
  CATEGORIA_OPCOES,
  CATEGORIA_ROTULO,
  PRIORIDADE_OPCOES,
  PRIORIDADE_ROTULO,
  STATUS_OPCOES,
  STATUS_ROTULO,
  formatarData,
  prioridadeClass,
  statusClass,
} from '../../shared/constants';

@Component({
  imports: [FormsModule, MatIconModule, RouterLink],
  selector: 'app-chamados',
  styleUrl: './chamados.css',
  templateUrl: './chamados.html',
})
export class Chamados implements OnInit {
  private readonly api = inject(ApiService);
  private readonly authService = inject(AuthService);

  readonly chamados = signal<Chamado[]>([]);
  readonly carregando = signal(true);
  readonly erro = signal('');
  readonly total = computed(() => this.chamados().length);
  readonly podeExcluir = computed(() => this.authService.usuario()?.perfil === 'ADMINISTRADOR');

  readonly statusOpcoes = STATUS_OPCOES;
  readonly prioridadeOpcoes = PRIORIDADE_OPCOES;
  readonly categoriaOpcoes = CATEGORIA_OPCOES;
  readonly statusRotulo = STATUS_ROTULO;
  readonly prioridadeRotulo = PRIORIDADE_ROTULO;
  readonly categoriaRotulo = CATEGORIA_ROTULO;
  readonly statusClass = statusClass;
  readonly prioridadeClass = prioridadeClass;
  readonly formatarData = formatarData;

  filtros: FiltrosChamado = {
    status: '',
    prioridade: '',
    categoria: '',
    busca: '',
  };

  ngOnInit(): void {
    this.listar();
  }

  listar(): void {
    this.carregando.set(true);
    this.erro.set('');
    this.api.listarChamados(this.filtros).subscribe({
      next: (chamados) => {
        this.chamados.set(chamados);
        this.carregando.set(false);
      },
      error: () => {
        this.carregando.set(false);
        this.erro.set('Não foi possível carregar os chamados. Verifique a conexão com o backend.');
      },
    });
  }

  limparFiltros(): void {
    this.filtros = { status: '', prioridade: '', categoria: '', busca: '' };
    this.listar();
  }

  excluir(chamado: Chamado): void {
    const confirmado = window.confirm(
      `Deseja excluir o chamado ${chamado.protocolo} — "${chamado.titulo}"? Esta ação não pode ser desfeita.`,
    );
    if (!confirmado) {
      return;
    }

    this.api.excluirChamado(chamado.id).subscribe({
      next: () => this.listar(),
      error: () => this.erro.set('Não foi possível excluir o chamado selecionado.'),
    });
  }
}
