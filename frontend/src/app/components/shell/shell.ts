import { Component, computed, inject, signal } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../../core/auth.service';
import { PERFIL_ROTULO, iniciais } from '../../shared/constants';

@Component({
  imports: [MatIconModule, RouterOutlet, RouterLink, RouterLinkActive],
  selector: 'app-shell',
  styleUrl: './shell.css',
  templateUrl: './shell.html',
})
export class Shell {
  private readonly router = inject(Router);
  readonly authService = inject(AuthService);

  readonly menuAberto = signal(false);
  readonly usuario = this.authService.usuario;
  readonly iniciais = computed(() => {
    const usuario = this.usuario();
    return usuario ? iniciais(usuario.nome) : 'TI';
  });
  readonly ehSolicitante = computed(() => this.usuario()?.perfil === 'SOLICITANTE');
  readonly perfilRotulo = PERFIL_ROTULO;

  alternarMenu(): void {
    this.menuAberto.update((aberto) => !aberto);
  }

  fecharMenu(): void {
    this.menuAberto.set(false);
  }

  sair(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
