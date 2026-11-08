import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ApiService } from '../../core/api.service';
import { AuthService } from '../../core/auth.service';
import { Chamado, StatusChamado, Usuario } from '../../core/models';
import {
  CATEGORIA_ROTULO,
  PRIORIDADE_ROTULO,
  STATUS_OPCOES,
  STATUS_ROTULO,
  formatarData,
  iniciais,
  prioridadeClass,
  statusClass,
} from '../../shared/constants';

@Component({
  imports: [FormsModule, MatIconModule, RouterLink],
  selector: 'app-chamado-detalhe',
  styleUrl: './chamado-detalhe.css',
  templateUrl: './chamado-detalhe.html',
})
export class ChamadoDetalhe implements OnInit {
  private readonly api = inject(ApiService);
  private readonly authService = inject(AuthService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  readonly chamado = signal<Chamado | null>(null);
  readonly usuarios = signal<Usuario[]>([]);
  readonly carregando = signal(true);
  readonly erro = signal('');
  readonly mensagem = signal('');
  readonly mensagemErro = signal('');
  readonly salvandoStatus = signal(false);
  readonly enviandoComentario = signal(false);

  readonly usuarioAtual = this.authService.usuario;
  readonly podeGerenciar = computed(() => this.usuarioAtual()?.perfil !== 'SOLICITANTE');
  readonly podeExcluir = computed(() => this.usuarioAtual()?.perfil === 'ADMINISTRADOR');

  readonly statusOpcoes = STATUS_OPCOES;
  readonly statusRotulo = STATUS_ROTULO;
  readonly prioridadeRotulo = PRIORIDADE_ROTULO;
  readonly categoriaRotulo = CATEGORIA_ROTULO;
  readonly statusClass = statusClass;
  readonly prioridadeClass = prioridadeClass;
  readonly formatarData = formatarData;
  readonly iniciais = iniciais;

  formStatus = {
    status: 'ABERTO' as StatusChamado,
    tecnicoId: null as number | null,
    solucao: '',
  };

  comentario = {
    autor: this.authService.usuario()?.nome ?? '',
    mensagem: '',
  };

  ngOnInit(): void {
    this.carregar();
    this.api.listarUsuarios().subscribe({
      next: (usuarios) => this.usuarios.set(usuarios),
      error: () => undefined,
    });
  }

  carregar(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (!id) {
      this.router.navigate(['/chamados']);
      return;
    }

    this.carregando.set(true);
    this.api.buscarChamado(id).subscribe({
      next: (chamado) => {
        this.chamado.set(chamado);
        this.formStatus = {
          status: chamado.status,
          tecnicoId: chamado.tecnicoId,
          solucao: chamado.solucao ?? '',
        };
        this.carregando.set(false);
      },
      error: () => {
        this.carregando.set(false);
        this.erro.set('Não foi possível carregar o chamado informado.');
      },
    });
  }

  atualizarStatus(): void {
    const chamado = this.chamado();
    if (!chamado) {
      return;
    }

    this.mensagem.set('');
    this.mensagemErro.set('');
    this.salvandoStatus.set(true);

    this.api
      .atualizarStatus(chamado.id, {
        status: this.formStatus.status,
        tecnicoId: this.formStatus.tecnicoId ? Number(this.formStatus.tecnicoId) : null,
        solucao: this.formStatus.solucao.trim() || null,
      })
      .subscribe({
        next: () => {
          this.salvandoStatus.set(false);
          this.mensagem.set('Status do chamado atualizado com sucesso.');
          this.carregar();
        },
        error: () => {
          this.salvandoStatus.set(false);
          this.mensagemErro.set('Não foi possível atualizar o status do chamado.');
        },
      });
  }

  comentar(): void {
    const chamado = this.chamado();
    if (!chamado) {
      return;
    }
    if (!this.comentario.mensagem.trim()) {
      this.mensagemErro.set('Escreva uma mensagem antes de enviar.');
      return;
    }

    this.mensagem.set('');
    this.mensagemErro.set('');
    this.enviandoComentario.set(true);

    this.api
      .adicionarComentario(chamado.id, {
        autor: this.comentario.autor,
        mensagem: this.comentario.mensagem.trim(),
      })
      .subscribe({
        next: () => {
          this.enviandoComentario.set(false);
          this.comentario.mensagem = '';
          this.mensagem.set('Comentário adicionado ao histórico.');
          this.carregar();
        },
        error: () => {
          this.enviandoComentario.set(false);
          this.mensagemErro.set('Não foi possível adicionar o comentário.');
        },
      });
  }

  excluir(): void {
    const chamado = this.chamado();
    if (!chamado) {
      return;
    }
    if (!window.confirm(`Deseja excluir o chamado ${chamado.protocolo}?`)) {
      return;
    }

    this.api.excluirChamado(chamado.id).subscribe({
      next: () => this.router.navigate(['/chamados']),
      error: () => this.mensagemErro.set('Não foi possível excluir o chamado.'),
    });
  }
}
