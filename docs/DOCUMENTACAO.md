# Documentação do Projeto — Portal de Chamados de TI

## 1. Identificação

- **Nome do projeto:** Portal de Chamados de TI
- **Instituição:** Faculdade Senac Pernambuco
- **Curso:** Análise e Desenvolvimento de Sistemas
- **Turma:** 185 — Equipe 4
- **Integrantes:** Ericha Tainá e Kennedy Veras
- **Proposta:** aplicação web para gestão de chamados de suporte de TI em uma instituição
  pública, integrando frontend Angular e backend Spring Boot.

## 2. Objetivo

Oferecer um canal único e organizado para que servidores e colaboradores registrem problemas
de TI e acompanhem o atendimento, enquanto a equipe técnica gerencia a fila de chamados,
atribui responsáveis, registra soluções e acompanha indicadores de prazo (SLA).

### Objetivos específicos

1. Reduzir a dispersão de solicitações por e-mail, telefone e mensagens informais.
2. Dar visibilidade ao solicitante sobre o andamento do atendimento.
3. Permitir priorização e distribuição de chamados entre os técnicos.
4. Produzir indicadores para acompanhamento da qualidade do suporte.
5. Demonstrar a integração completa entre uma API REST Java e uma SPA Angular.

## 3. Escopo funcional

### 3.1 Atores

| Ator | Descrição |
| --- | --- |
| Solicitante | Abre chamados, acompanha status e adiciona comentários. |
| Técnico | Atende chamados, assume a responsabilidade, atualiza status e registra solução. |
| Administrador | Tem as mesmas permissões do técnico e também pode excluir chamados. |

### 3.2 Histórias de usuário

- Como solicitante, quero abrir um chamado informando título, descrição, categoria,
  prioridade e local.
- Como solicitante, quero acompanhar o status e o histórico do meu chamado.
- Como técnico, quero assumir um chamado e atualizar seu status.
- Como técnico, quero registrar comentários e a solução aplicada.
- Como administrador, quero visualizar indicadores gerais de atendimento.
- Como administrador, quero excluir chamados indevidos ou duplicados.

### 3.3 Requisitos funcionais

| Código | Requisito |
| --- | --- |
| RF01 | Autenticar usuário por e-mail institucional |
| RF02 | Listar chamados com filtros de status, prioridade, categoria e texto |
| RF03 | Cadastrar chamado com protocolo automático |
| RF04 | Editar dados do chamado |
| RF05 | Excluir chamado (administrador) |
| RF06 | Atribuir técnico responsável |
| RF07 | Atualizar status e registrar solução |
| RF08 | Adicionar e listar comentários do histórico |
| RF09 | Calcular prazo de SLA conforme prioridade |
| RF10 | Exibir dashboard com indicadores agregados |

### 3.4 Requisitos não funcionais

- API REST com respostas JSON e códigos HTTP adequados.
- Validação de dados de entrada com Bean Validation.
- Tratamento centralizado de erros.
- Interface responsiva e acessível em desktop e dispositivos móveis.
- Código organizado em camadas (controller, service, repository, dto, model).
- Persistência em H2 para demonstração sem instalação de servidor de banco.

## 4. Arquitetura

```
┌──────────────────────────┐        HTTP/JSON        ┌──────────────────────────┐
│  Frontend Angular 22     │  ───────────────────▶   │  Backend Spring Boot 4   │
│                          │                         │                          │
│  Pages / Components      │  ◀───────────────────   │  Controllers             │
│  Services (HttpClient)   │                         │  Services                │
│  Models / Signals        │                         │  Repositories (JPA)      │
└──────────────────────────┘                         └────────────┬─────────────┘
                                                                  │
                                                                  ▼
                                                         ┌──────────────────┐
                                                         │ H2 Database      │
                                                         │ (em memória)     │
                                                         └──────────────────┘
```

### 4.1 Backend

- **Controller:** expõe os endpoints REST e recebe/valida os DTOs.
- **Service:** concentra regras de negócio, cálculo de SLA, protocolo e transições de status.
- **Repository:** interfaces Spring Data JPA.
- **Model:** entidades JPA (`Usuario`, `Chamado`, `Comentario`) e enums de domínio.
- **DTO:** contratos `Request`/`Response`, evitando serialização direta das entidades.
- **Config:** CORS e `DataInitializer` com dados de demonstração.
- **Exception:** tratamento global com respostas padronizadas.

### 4.2 Frontend

- **Core:** modelos TypeScript, serviço HTTP, serviço de autenticação e guards de rota.
- **Shared:** rótulos, opções de formulário e funções utilitárias de formatação.
- **Components:** `Shell` (sidebar, topbar e `router-outlet`), `PageHeader`, `StatusBadge`,
  `PriorityBadge`, `TicketFilters`, `TicketTable`, `DashboardStats`, `TicketTimeline` e
  `TicketStatusForm`.
- **Pages:** Login, Dashboard, Lista de Chamados, Meus Chamados, Formulário e Detalhe do Chamado.

### 4.3 Rotas do frontend

| Rota | Componente | Acesso | Descrição |
| --- | --- | --- | --- |
| `/login` | `Login` | Público | Entrada no portal |
| `/dashboard` | `Dashboard` | Administrador | Indicadores e chamados recentes |
| `/meus-chamados` | `MeusChamados` | Solicitante | Chamados abertos pelo usuário logado |
| `/meus-chamados/:id` | `ChamadoDetalhe` | Solicitante | Detalhe de um chamado do próprio solicitante |
| `/chamados` | `Chamados` | Técnico e administrador | Fila geral com filtros |
| `/chamados/novo` | `ChamadoForm` | Autenticado | Abertura de chamado |
| `/chamados/:id` | `ChamadoDetalhe` | Autenticado | Detalhe, histórico e gestão |
| `/chamados/:id/editar` | `ChamadoForm` | Técnico e administrador | Edição do chamado |

Rotas internas são protegidas pelo `authGuard`, que verifica a sessão armazenada em
`sessionStorage`. Os guards `dashboardGuard`, `staffGuard` e `solicitanteGuard` aplicam as
restrições por perfil. O detalhe também valida, no frontend, que o solicitante só acesse os
próprios chamados.

## 5. Modelo de dados

### 5.1 Usuario

| Campo | Tipo | Observações |
| --- | --- | --- |
| id | Long | Chave primária |
| nome | String | Obrigatório, até 120 caracteres |
| email | String | Obrigatório, único |
| setor | String | Obrigatório |
| perfil | Enum | `SOLICITANTE`, `TECNICO`, `ADMINISTRADOR` |
| ativo | boolean | Usuários inativos não aparecem nas listas |

### 5.2 Chamado

| Campo | Tipo | Observações |
| --- | --- | --- |
| id | Long | Chave primária |
| protocolo | String | Único, gerado como `TI-ANO-ID` |
| titulo | String | Obrigatório, até 160 caracteres |
| descricao | String | Obrigatório, até 2000 caracteres |
| categoria | Enum | Hardware, software, rede etc. |
| prioridade | Enum | `BAIXA`, `MEDIA`, `ALTA`, `CRITICA` |
| status | Enum | Aberto, em andamento, aguardando usuário, resolvido, fechado |
| solicitante | Usuario | Many-to-one obrigatório |
| tecnico | Usuario | Many-to-one opcional |
| local | String | Setor ou local físico |
| dataAbertura | LocalDateTime | Preenchida na criação |
| dataAtualizacao | LocalDateTime | Atualizada a cada mudança |
| dataFechamento | LocalDateTime | Preenchida ao resolver/fechar |
| prazoSla | LocalDateTime | Abertura + prazo da prioridade |
| solucao | String | Registrada no atendimento |
| comentarios | List<Comentario> | Histórico de interações |

### 5.3 Comentario

| Campo | Tipo | Observações |
| --- | --- | --- |
| id | Long | Chave primária |
| chamado | Chamado | Many-to-one obrigatório |
| autor | String | Nome de quem comentou |
| mensagem | String | Até 2000 caracteres |
| dataComentario | LocalDateTime | Preenchida automaticamente |

### 5.4 Relacionamentos

- Um `Usuario` pode ser solicitante de vários `Chamado`.
- Um `Usuario` técnico pode ser responsável por vários `Chamado`.
- Um `Chamado` possui vários `Comentario` (exclusão em cascata).

## 6. Contratos da API

Base URL: `http://localhost:8080`

### 6.1 Autenticação

`POST /auth/login`

```json
{ "email": "kennedy@senac.pe.gov.br" }
```

Resposta `200 OK`:

```json
{
  "id": 2,
  "nome": "Kennedy Veras",
  "email": "kennedy@senac.pe.gov.br",
  "setor": "Suporte Tecnico",
  "perfil": "TECNICO",
  "ativo": true
}
```

### 6.2 Usuários

`GET /usuarios` — retorna apenas usuários ativos, ordenados por nome.

### 6.3 Listagem de chamados

`GET /chamados?status=ABERTO&prioridade=ALTA&categoria=HARDWARE&tecnicoId=2&busca=impressora`

Todos os parâmetros são opcionais e combináveis. A busca textual considera protocolo, título e
descrição, sem diferenciar maiúsculas de minúsculas.

### 6.4 Detalhe

`GET /chamados/{id}` — retorna o chamado com a lista de comentários.

### 6.5 Criação

`POST /chamados`

```json
{
  "titulo": "Computador não liga",
  "descricao": "O equipamento não apresenta sinal de vídeo.",
  "categoria": "HARDWARE",
  "prioridade": "ALTA",
  "solicitanteId": 4,
  "tecnicoId": 2,
  "local": "Secretaria Acadêmica - Sala 12"
}
```

Resposta `201 Created` com o chamado criado e protocolo gerado.

### 6.6 Atualização

`PUT /chamados/{id}` — recebe o mesmo corpo da criação.

### 6.7 Atualização de status

`PATCH /chamados/{id}/status`

```json
{
  "status": "RESOLVIDO",
  "tecnicoId": 2,
  "solucao": "Fonte substituída e equipamento testado."
}
```

Reabertura: `PATCH /chamados/{id}/reabrir`. Apenas chamados `RESOLVIDO` ou `FECHADO` podem ser
reabertos; o status volta para `EM_ANDAMENTO` e a data de fechamento é limpa.

### 6.8 Comentários

`POST /chamados/{id}/comentarios`

```json
{
  "autor": "Diego Alves",
  "mensagem": "Peça solicitada ao almoxarifado."
}
```

### 6.9 Exclusão

`DELETE /chamados/{id}` — resposta `204 No Content`.

### 6.10 Dashboard

`GET /chamados/dashboard`

```json
{
  "total": 10,
  "abertos": 3,
  "emAndamento": 2,
  "aguardandoUsuario": 1,
  "resolvidos": 2,
  "fechados": 2,
  "criticos": 1,
  "slaViolados": 2,
  "percentualResolvidos": 40.0,
  "porCategoria": [{ "chave": "HARDWARE", "total": 2 }],
  "porPrioridade": [{ "chave": "ALTA", "total": 4 }],
  "recentes": []
}
```

### 6.11 Erros

Erros de validação retornam `400 Bad Request`:

```json
{
  "timestamp": "2026-11-08T12:00:00",
  "status": 400,
  "mensagem": "Dados invalidos",
  "campos": { "titulo": "O titulo e obrigatorio" }
}
```

Recursos inexistentes retornam `404 Not Found` com a mensagem do recurso.

## 7. Integração frontend ↔ backend

1. O usuário informa o e-mail na tela de login.
2. O frontend chama `POST /auth/login` e armazena o usuário na sessão do navegador.
3. O `authGuard` impede o acesso às rotas internas sem sessão.
4. As páginas chamam os endpoints por meio do `ApiService`.
5. O backend processa a regra de negócio, persiste no H2 e devolve JSON.
6. O frontend atualiza os signals e re-renderiza a interface.
7. Datas ISO são formatadas em pt-BR; enums são traduzidos por mapas de rótulos.

O CORS do backend permite as origens `http://localhost:4200` e `http://localhost:4201`.

## 8. Configuração e execução

### 8.1 Backend

Pré-requisito: Java 17 ou superior.

```bash
cd backend
./mvnw spring-boot:run      # Linux/macOS
mvnw.cmd spring-boot:run    # Windows
```

- API: `http://localhost:8080`
- H2 Console: `http://localhost:8080/h2-console`
  - URL: `jdbc:h2:mem:chamadosdb`
  - Usuário: `sa`
  - Senha: vazia

Testes:

```bash
cd backend
./mvnw test
```

### 8.2 Frontend

Pré-requisito: Node.js 22 ou superior.

```bash
cd frontend
npm install
npm start
```

- Aplicação: `http://localhost:4200`
- Build: `npm run build`

## 9. Validação realizada

- Compilação do backend com `./mvnw clean test` (2 testes de contexto e dados iniciais).
- Compilação do frontend com `npm run build`.
- Testes manuais dos endpoints com `curl`:
  - login por e-mail;
  - listagem e filtros;
  - criação de chamado com protocolo automático;
  - inclusão de comentário;
  - atualização de status e solução;
  - exclusão com resposta `204`.
- Seed com 6 usuários, 10 chamados, comentários e diferentes status/prioridades.

## 10. Decisões técnicas e limitações

### Decisões

- Uso de DTOs para desacoplar o contrato da API das entidades JPA.
- Regras de SLA e protocolo na camada de serviço, mantendo controllers enxutos.
- `DataInitializer` para garantir uma demonstração consistente em qualquer máquina.
- H2 em memória para simplificar a correção e a apresentação.
- Angular standalone com signals e control flow moderno.

### Limitações e evoluções

- A autenticação é simulada por e-mail; não há senha nem token. Em produção, usar
  Spring Security com JWT.
- A autorização de perfis é aplicada na interface; o backend não bloqueia operações por perfil.
- Não há paginação; a lista retorna todos os chamados compatíveis com os filtros.
- O banco é recriado a cada execução; para persistência, migrar para PostgreSQL com Flyway.
- Possíveis evoluções: anexos, notificações, relatórios exportáveis e auditoria.
