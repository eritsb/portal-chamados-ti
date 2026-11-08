import { Injectable, signal } from '@angular/core';
import { Usuario } from './models';

const CHAVE_SESSAO = 'portal-chamados.usuario';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly usuarioAtual = signal<Usuario | null>(this.recuperarSessao());

  readonly usuario = this.usuarioAtual.asReadonly();

  login(usuario: Usuario): void {
    sessionStorage.setItem(CHAVE_SESSAO, JSON.stringify(usuario));
    this.usuarioAtual.set(usuario);
  }

  logout(): void {
    sessionStorage.removeItem(CHAVE_SESSAO);
    this.usuarioAtual.set(null);
  }

  estaLogado(): boolean {
    return this.usuarioAtual() !== null;
  }

  private recuperarSessao(): Usuario | null {
    const salvo = sessionStorage.getItem(CHAVE_SESSAO);
    if (!salvo) {
      return null;
    }
    try {
      return JSON.parse(salvo) as Usuario;
    } catch {
      sessionStorage.removeItem(CHAVE_SESSAO);
      return null;
    }
  }
}
