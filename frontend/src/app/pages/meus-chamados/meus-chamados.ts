import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { RouterLink } from '@angular/router';
import { ApiService } from '../../core/api.service';
import { AuthService } from '../../core/auth.service';
import { Chamado } from '../../core/models';
import {
  CATEGORIA_ROTULO,
  PRIORIDADE_ROTULO,
  STATUS_ROTULO,
  formatarData,
  prioridadeClass,
  statusClass,
} from '../../shared/constants';

@Component({
  imports: [MatIconModule, RouterLink],
  selector: 'app-meus-chamados',
  styleUrl: './meus-chamados.css',
  templateUrl: './meus-chamados.html',
})
export class MeusChamados implements OnInit {
  private readonly api = inject(ApiService);
  private readonly authService = inject(AuthService);

  readonly chamados = signal<Chamado[]>([]);
  readonly carregando = signal(true);
  readonly erro = signal('');
  readonly usuario = this.authService.usuario;
  readonly total = computed(() => this.chamados().length);

  readonly categoriaRotulo = CATEGORIA_ROTULO;
  readonly prioridadeRotulo = PRIORIDADE_ROTULO;
  readonly statusRotulo = STATUS_ROTULO;
  readonly statusClass = statusClass;
  readonly prioridadeClass = prioridadeClass;
  readonly formatarData = formatarData;

  ngOnInit(): void {
    this.carregar();
  }

  carregar(): void {
    const usuario = this.usuario();
    if (!usuario) {
      this.carregando.set(false);
      return;
    }

    this.carregando.set(true);
    this.erro.set('');
    this.api.listarChamados().subscribe({
      next: (chamados) => {
        const meusChamados = chamados
          .filter((chamado) => chamado.solicitanteId === usuario.id)
          .sort(
            (a, b) =>
              new Date(b.dataAbertura).getTime() - new Date(a.dataAbertura).getTime(),
          );
        this.chamados.set(meusChamados);
        this.carregando.set(false);
      },
      error: () => {
        this.carregando.set(false);
        this.erro.set('Não foi possível carregar seus chamados. Verifique a conexão com o backend.');
      },
    });
  }
}
