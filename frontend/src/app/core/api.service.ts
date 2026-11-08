import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import {
  AtualizarStatusRequest,
  Chamado,
  ChamadoRequest,
  Comentario,
  ComentarioRequest,
  Dashboard,
  FiltrosChamado,
  Usuario,
} from './models';

@Injectable({ providedIn: 'root' })
export class ApiService {
  private readonly apiUrl = 'http://localhost:8080';

  constructor(private readonly http: HttpClient) {}

  login(email: string): Observable<Usuario> {
    return this.http.post<Usuario>(`${this.apiUrl}/auth/login`, { email });
  }

  listarUsuarios(): Observable<Usuario[]> {
    return this.http.get<Usuario[]>(`${this.apiUrl}/usuarios`);
  }

  dashboard(): Observable<Dashboard> {
    return this.http.get<Dashboard>(`${this.apiUrl}/chamados/dashboard`);
  }

  listarChamados(filtros: FiltrosChamado = {}): Observable<Chamado[]> {
    let params = new HttpParams();
    if (filtros.status) {
      params = params.set('status', filtros.status);
    }
    if (filtros.prioridade) {
      params = params.set('prioridade', filtros.prioridade);
    }
    if (filtros.categoria) {
      params = params.set('categoria', filtros.categoria);
    }
    if (filtros.busca?.trim()) {
      params = params.set('busca', filtros.busca.trim());
    }
    return this.http.get<Chamado[]>(`${this.apiUrl}/chamados`, { params });
  }

  buscarChamado(id: number): Observable<Chamado> {
    return this.http.get<Chamado>(`${this.apiUrl}/chamados/${id}`);
  }

  criarChamado(chamado: ChamadoRequest): Observable<Chamado> {
    return this.http.post<Chamado>(`${this.apiUrl}/chamados`, chamado);
  }

  atualizarChamado(id: number, chamado: ChamadoRequest): Observable<Chamado> {
    return this.http.put<Chamado>(`${this.apiUrl}/chamados/${id}`, chamado);
  }

  atualizarStatus(id: number, dados: AtualizarStatusRequest): Observable<Chamado> {
    return this.http.patch<Chamado>(`${this.apiUrl}/chamados/${id}/status`, dados);
  }

  adicionarComentario(id: number, comentario: ComentarioRequest): Observable<Comentario> {
    return this.http.post<Comentario>(`${this.apiUrl}/chamados/${id}/comentarios`, comentario);
  }

  excluirChamado(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/chamados/${id}`);
  }
}
