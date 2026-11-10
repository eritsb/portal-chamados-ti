import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { Router } from '@angular/router';
import { ApiService } from '../../core/api.service';
import { AuthService } from '../../core/auth.service';
import { Usuario } from '../../core/models';
import { PERFIL_ROTULO, iniciais } from '../../shared/constants';

@Component({
  imports: [FormsModule, MatIconModule],
  selector: 'app-login',
  styleUrl: './login.css',
  templateUrl: './login.html',
})
export class Login implements OnInit {
  private readonly api = inject(ApiService);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  email = '';
  senha = '';
  readonly carregando = signal(false);
  readonly erro = signal('');
  readonly usuariosDemo = signal<Usuario[]>([]);
  readonly perfilRotulo = PERFIL_ROTULO;
  readonly iniciais = iniciais;
  readonly anoAtual = new Date().getFullYear();

  ngOnInit(): void {
    const usuario = this.authService.usuario();
    if (usuario) {
      this.router.navigate([this.destino(usuario)]);
      return;
    }
    this.api.listarUsuarios().subscribe({
      next: (usuarios) => this.usuariosDemo.set(usuarios),
      error: () => undefined,
    });
  }

  entrar(): void {
    this.erro.set('');

    if (!this.email.trim()) {
      this.erro.set('Informe o e-mail institucional para continuar.');
      return;
    }

    this.carregando.set(true);
    this.api.login(this.email.trim()).subscribe({
      next: (usuario) => {
        this.authService.login(usuario);
        this.router.navigate([this.destino(usuario)]);
      },
      error: () => {
        this.carregando.set(false);
        this.erro.set('E-mail não encontrado ou usuário inativo. Verifique os dados e tente novamente.');
      },
    });
  }

  usarConta(usuario: Usuario): void {
    this.email = usuario.email;
    this.senha = 'demo';
    this.entrar();
  }

  private destino(usuario: Usuario): string {
    return usuario.perfil === 'SOLICITANTE' ? '/meus-chamados' : '/dashboard';
  }
}
