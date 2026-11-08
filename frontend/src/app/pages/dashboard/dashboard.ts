import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { RouterLink } from '@angular/router';
import { ApiService } from '../../core/api.service';
import { AuthService } from '../../core/auth.service';
import { CategoriaChamado, Dashboard as DashboardDados, PrioridadeChamado } from '../../core/models';
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
  selector: 'app-dashboard',
  styleUrl: './dashboard.css',
  templateUrl: './dashboard.html',
})
export class Dashboard implements OnInit {
  private readonly api = inject(ApiService);
  private readonly authService = inject(AuthService);

  readonly dados = signal<DashboardDados | null>(null);
  readonly carregando = signal(true);
  readonly erro = signal('');

  readonly usuario = this.authService.usuario;
  readonly categoriaRotulo = CATEGORIA_ROTULO;
  readonly prioridadeRotulo = PRIORIDADE_ROTULO;
  readonly statusRotulo = STATUS_ROTULO;
  readonly statusClass = statusClass;
  readonly prioridadeClass = prioridadeClass;
  readonly formatarData = formatarData;

  readonly saudacao = computed(() => {
    const hora = new Date().getHours();
    if (hora < 12) {
      return 'Bom dia';
    }
    if (hora < 18) {
      return 'Boa tarde';
    }
    return 'Boa noite';
  });

  readonly maiorCategoria = computed(() => {
    const totais = this.dados()?.porCategoria.map((item) => item.total) ?? [];
    return Math.max(1, ...totais);
  });

  ngOnInit(): void {
    this.carregar();
  }

  carregar(): void {
    this.carregando.set(true);
    this.erro.set('');
    this.api.dashboard().subscribe({
      next: (dados) => {
        this.dados.set(dados);
        this.carregando.set(false);
      },
      error: () => {
        this.carregando.set(false);
        this.erro.set('Não foi possível carregar os indicadores. Verifique se o backend está em execução.');
      },
    });
  }

  percentualCategoria(total: number): number {
    return Math.max(4, Math.round((total / this.maiorCategoria()) * 100));
  }

  rotuloCategoria(chave: string): string {
    return CATEGORIA_ROTULO[chave as CategoriaChamado] ?? chave;
  }

  rotuloPrioridade(chave: string): string {
    return PRIORIDADE_ROTULO[chave as PrioridadeChamado] ?? chave;
  }

  prioridadeClasse(chave: string): string {
    return prioridadeClass(chave as PrioridadeChamado);
  }
}
