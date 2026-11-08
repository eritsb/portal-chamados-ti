import { Routes } from '@angular/router';
import { authGuard } from './core/auth.guard';
import { Shell } from './components/shell/shell';
import { Login } from './pages/login/login';
import { Dashboard } from './pages/dashboard/dashboard';
import { Chamados } from './pages/chamados/chamados';
import { ChamadoForm } from './pages/chamado-form/chamado-form';
import { ChamadoDetalhe } from './pages/chamado-detalhe/chamado-detalhe';

export const routes: Routes = [
  { path: 'login', component: Login },
  {
    path: '',
    component: Shell,
    canActivate: [authGuard],
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', component: Dashboard },
      { path: 'chamados', component: Chamados },
      { path: 'chamados/novo', component: ChamadoForm },
      { path: 'chamados/:id', component: ChamadoDetalhe },
      { path: 'chamados/:id/editar', component: ChamadoForm },
    ],
  },
  { path: '**', redirectTo: '' },
];
