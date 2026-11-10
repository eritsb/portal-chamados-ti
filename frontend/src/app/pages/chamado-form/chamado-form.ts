import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ApiService } from '../../core/api.service';
import { AuthService } from '../../core/auth.service';
import { ChamadoRequest, Usuario } from '../../core/models';
import { CATEGORIA_OPCOES, PRIORIDADE_OPCOES, PRIORIDADE_ROTULO } from '../../shared/constants';

@Component({
  imports: [FormsModule, MatIconModule, RouterLink],
  selector: 'app-chamado-form',
  styleUrl: './chamado-form.css',
  templateUrl: './chamado-form.html',
})
export class ChamadoForm implements OnInit {
  private readonly api = inject(ApiService);
  private readonly authService = inject(AuthService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  readonly id = signal<number | null>(null);
  readonly editando = computed(() => this.id() !== null);
  readonly usuario = this.authService.usuario;
  readonly ehSolicitante = computed(() => this.authService.usuario()?.perfil === 'SOLICITANTE');
  readonly linkLista = computed(() => (this.ehSolicitante() ? '/meus-chamados' : '/chamados'));
  readonly usuarios = signal<Usuario[]>([]);
  readonly carregando = signal(false);
  readonly salvando = signal(false);
  readonly erro = signal('');

  readonly categoriaOpcoes = CATEGORIA_OPCOES;
  readonly prioridadeOpcoes = PRIORIDADE_OPCOES;
  readonly prioridadeRotulo = PRIORIDADE_ROTULO;

  chamado: ChamadoRequest = {
    titulo: '',
    descricao: '',
    categoria: 'HARDWARE',
    prioridade: 'MEDIA',
    solicitanteId: 0,
    tecnicoId: null,
    local: '',
  };

  ngOnInit(): void {
    const parametro = this.route.snapshot.paramMap.get('id');
    this.id.set(parametro ? Number(parametro) : null);

    if (this.ehSolicitante()) {
      if (this.editando()) {
        this.router.navigate(['/meus-chamados']);
        return;
      }

      const usuario = this.authService.usuario();
      if (usuario) {
        this.chamado.solicitanteId = usuario.id;
        this.chamado.tecnicoId = null;
        if (!this.chamado.local) {
          this.chamado.local = usuario.setor;
        }
      }
      return;
    }

    this.api.listarUsuarios().subscribe({
      next: (usuarios) => {
        this.usuarios.set(usuarios);
        if (!this.editando() && !this.chamado.solicitanteId) {
          const usuario = this.authService.usuario();
          if (usuario) {
            this.chamado.solicitanteId = usuario.id;
          }
        }
      },
      error: () => this.erro.set('Não foi possível carregar a lista de usuários.'),
    });

    if (this.editando()) {
      this.carregar();
    }
  }

  carregar(): void {
    const id = this.id();
    if (id === null) {
      return;
    }

    this.carregando.set(true);
    this.api.buscarChamado(id).subscribe({
      next: (chamado) => {
        this.chamado = {
          titulo: chamado.titulo,
          descricao: chamado.descricao,
          categoria: chamado.categoria,
          prioridade: chamado.prioridade,
          solicitanteId: chamado.solicitanteId,
          tecnicoId: chamado.tecnicoId,
          local: chamado.local ?? '',
        };
        this.carregando.set(false);
      },
      error: () => {
        this.carregando.set(false);
        this.erro.set('Não foi possível carregar os dados do chamado.');
      },
    });
  }

  salvar(): void {
    this.erro.set('');

    if (!this.chamado.titulo.trim()) {
      this.erro.set('Informe o título do chamado.');
      return;
    }
    if (!this.chamado.descricao.trim()) {
      this.erro.set('Descreva o problema para que a equipe de TI possa atender.');
      return;
    }
    if (!this.chamado.solicitanteId) {
      this.erro.set('Selecione o solicitante do chamado.');
      return;
    }

    this.salvando.set(true);
    const usuario = this.authService.usuario();
    const ehSolicitante = usuario?.perfil === 'SOLICITANTE';
    const payload: ChamadoRequest = {
      ...this.chamado,
      titulo: this.chamado.titulo.trim(),
      descricao: this.chamado.descricao.trim(),
      solicitanteId: ehSolicitante && usuario ? usuario.id : this.chamado.solicitanteId,
      local: this.chamado.local?.trim() ? this.chamado.local.trim() : null,
      tecnicoId: ehSolicitante ? null : this.chamado.tecnicoId ? Number(this.chamado.tecnicoId) : null,
    };

    const id = this.id();
    const requisicao =
      id === null ? this.api.criarChamado(payload) : this.api.atualizarChamado(id, payload);

    requisicao.subscribe({
      next: (chamado) =>
        this.router.navigate(
          this.ehSolicitante()
            ? ['/meus-chamados', chamado.id]
            : ['/chamados', chamado.id],
        ),
      error: (erro) => {
        this.salvando.set(false);
        this.erro.set(this.mensagemDeErro(erro));
      },
    });
  }

  private mensagemDeErro(erro: unknown): string {
    const corpo = (erro as { error?: { mensagem?: string } })?.error;
    return corpo?.mensagem ?? 'Não foi possível salvar o chamado. Tente novamente.';
  }
}
