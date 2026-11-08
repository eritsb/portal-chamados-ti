import {
  CategoriaChamado,
  Perfil,
  PrioridadeChamado,
  StatusChamado,
} from '../core/models';

export interface Opcao<T> {
  valor: T;
  rotulo: string;
}

export const STATUS_OPCOES: Opcao<StatusChamado>[] = [
  { valor: 'ABERTO', rotulo: 'Aberto' },
  { valor: 'EM_ANDAMENTO', rotulo: 'Em andamento' },
  { valor: 'AGUARDANDO_USUARIO', rotulo: 'Aguardando usuário' },
  { valor: 'RESOLVIDO', rotulo: 'Resolvido' },
  { valor: 'FECHADO', rotulo: 'Fechado' },
];

export const PRIORIDADE_OPCOES: Opcao<PrioridadeChamado>[] = [
  { valor: 'BAIXA', rotulo: 'Baixa' },
  { valor: 'MEDIA', rotulo: 'Média' },
  { valor: 'ALTA', rotulo: 'Alta' },
  { valor: 'CRITICA', rotulo: 'Crítica' },
];

export const CATEGORIA_OPCOES: Opcao<CategoriaChamado>[] = [
  { valor: 'HARDWARE', rotulo: 'Hardware' },
  { valor: 'SOFTWARE', rotulo: 'Software' },
  { valor: 'REDE', rotulo: 'Rede / Internet' },
  { valor: 'ACESSO_SENHA', rotulo: 'Acesso e senha' },
  { valor: 'IMPRESSORA', rotulo: 'Impressora' },
  { valor: 'TELEFONIA', rotulo: 'Telefonia' },
  { valor: 'OUTROS', rotulo: 'Outros' },
];

export const PERFIL_ROTULO: Record<Perfil, string> = {
  SOLICITANTE: 'Solicitante',
  TECNICO: 'Técnico de TI',
  ADMINISTRADOR: 'Administrador',
};

export const STATUS_ROTULO: Record<StatusChamado, string> = {
  ABERTO: 'Aberto',
  EM_ANDAMENTO: 'Em andamento',
  AGUARDANDO_USUARIO: 'Aguardando usuário',
  RESOLVIDO: 'Resolvido',
  FECHADO: 'Fechado',
};

export const PRIORIDADE_ROTULO: Record<PrioridadeChamado, string> = {
  BAIXA: 'Baixa',
  MEDIA: 'Média',
  ALTA: 'Alta',
  CRITICA: 'Crítica',
};

export const CATEGORIA_ROTULO: Record<CategoriaChamado, string> = {
  HARDWARE: 'Hardware',
  SOFTWARE: 'Software',
  REDE: 'Rede / Internet',
  ACESSO_SENHA: 'Acesso e senha',
  IMPRESSORA: 'Impressora',
  TELEFONIA: 'Telefonia',
  OUTROS: 'Outros',
};

export function statusClass(status: StatusChamado): string {
  return `b-status-${status.toLowerCase()}`;
}

export function prioridadeClass(prioridade: PrioridadeChamado): string {
  return `b-prio-${prioridade.toLowerCase()}`;
}

export function formatarData(valor: string | null | undefined): string {
  if (!valor) {
    return '—';
  }
  return new Intl.DateTimeFormat('pt-BR', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  }).format(new Date(valor));
}

export function formatarDataCurta(valor: string | null | undefined): string {
  if (!valor) {
    return '—';
  }
  return new Intl.DateTimeFormat('pt-BR', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
  }).format(new Date(valor));
}

export function iniciais(nome: string): string {
  const partes = nome.trim().split(/\s+/);
  if (partes.length === 1) {
    return partes[0].slice(0, 2).toUpperCase();
  }
  return `${partes[0][0]}${partes[partes.length - 1][0]}`.toUpperCase();
}
