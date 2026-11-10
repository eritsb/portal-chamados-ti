import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';

export const authGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.estaLogado()) {
    return true;
  }

  return router.createUrlTree(['/login']);
};

export const staffGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const usuario = authService.usuario();

  if (!usuario) {
    return router.createUrlTree(['/login']);
  }

  if (usuario.perfil === 'SOLICITANTE') {
    return router.createUrlTree(['/meus-chamados']);
  }

  return true;
};

export const solicitanteGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const usuario = authService.usuario();

  if (!usuario) {
    return router.createUrlTree(['/login']);
  }

  if (usuario.perfil !== 'SOLICITANTE') {
    return router.createUrlTree(['/chamados']);
  }

  return true;
};
