export type Perfil = 'SOLICITANTE' | 'TECNICO' | 'ADMINISTRADOR';

export type StatusChamado =
  | 'ABERTO'
  | 'EM_ANDAMENTO'
  | 'AGUARDANDO_USUARIO'
  | 'RESOLVIDO'
  | 'FECHADO';

export type PrioridadeChamado = 'BAIXA' | 'MEDIA' | 'ALTA' | 'CRITICA';

export type CategoriaChamado =
  | 'HARDWARE'
  | 'SOFTWARE'
  | 'REDE'
  | 'ACESSO_SENHA'
  | 'IMPRESSORA'
  | 'TELEFONIA'
  | 'OUTROS';

export interface Usuario {
  id: number;
  nome: string;
  email: string;
  setor: string;
  perfil: Perfil;
  ativo: boolean;
}

export interface Comentario {
  id: number;
  autor: string;
  mensagem: string;
  dataComentario: string;
}

export interface Chamado {
  id: number;
  protocolo: string;
  titulo: string;
  descricao: string;
  categoria: CategoriaChamado;
  prioridade: PrioridadeChamado;
  status: StatusChamado;
  solicitanteId: number;
  solicitanteNome: string;
  solicitanteSetor: string;
  tecnicoId: number | null;
  tecnicoNome: string | null;
  local: string | null;
  dataAbertura: string;
  dataAtualizacao: string;
  dataFechamento: string | null;
  prazoSla: string;
  slaViolado: boolean;
  solucao: string | null;
  comentarios: Comentario[];
}

export interface ChamadoRequest {
  titulo: string;
  descricao: string;
  categoria: CategoriaChamado;
  prioridade: PrioridadeChamado;
  solicitanteId: number;
  tecnicoId: number | null;
  local: string | null;
}

export interface AtualizarStatusRequest {
  status: StatusChamado;
  tecnicoId: number | null;
  solucao: string | null;
}

export interface ComentarioRequest {
  autor: string;
  mensagem: string;
}

export interface Contagem {
  chave: string;
  total: number;
}

export interface Dashboard {
  total: number;
  abertos: number;
  emAndamento: number;
  aguardandoUsuario: number;
  resolvidos: number;
  fechados: number;
  criticos: number;
  slaViolados: number;
  percentualResolvidos: number;
  porCategoria: Contagem[];
  porPrioridade: Contagem[];
  recentes: Chamado[];
}

export interface FiltrosChamado {
  status?: StatusChamado | '';
  prioridade?: PrioridadeChamado | '';
  categoria?: CategoriaChamado | '';
  busca?: string;
}
