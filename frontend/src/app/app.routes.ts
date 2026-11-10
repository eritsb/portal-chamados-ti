import { Routes } from '@angular/router';
import { authGuard, solicitanteGuard, staffGuard } from './core/auth.guard';
import { Shell } from './components/shell/shell';
import { Login } from './pages/login/login';
import { Dashboard } from './pages/dashboard/dashboard';
import { Chamados } from './pages/chamados/chamados';
import { ChamadoForm } from './pages/chamado-form/chamado-form';
import { ChamadoDetalhe } from './pages/chamado-detalhe/chamado-detalhe';
import { MeusChamados } from './pages/meus-chamados/meus-chamados';

export const routes: Routes = [
  { path: 'login', component: Login },
  {
    path: '',
    component: Shell,
    canActivate: [authGuard],
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', component: Dashboard, canActivate: [staffGuard] },
      { path: 'meus-chamados', component: MeusChamados, canActivate: [solicitanteGuard] },
      { path: 'meus-chamados/:id', component: ChamadoDetalhe, canActivate: [solicitanteGuard] },
      { path: 'chamados', component: Chamados, canActivate: [staffGuard] },
      { path: 'chamados/novo', component: ChamadoForm },
      { path: 'chamados/:id', component: ChamadoDetalhe },
      { path: 'chamados/:id/editar', component: ChamadoForm, canActivate: [staffGuard] },
    ],
  },
  { path: '**', redirectTo: '' },
];
