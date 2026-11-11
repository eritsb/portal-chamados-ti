# Portal de Chamados de TI

Sistema web full stack para abertura, acompanhamento e gestão de chamados de suporte de TI em
uma instituição pública. O projeto integra um backend em **Java + Spring Boot** a um frontend em
**Angular**, com persistência em **H2** e dados de demonstração pré-carregados.

> Projeto Final — Residência Porto Digital / SENAC 3º Período — CAPGEMINI
>
> Repositório: <https://github.com/eritsb/portal-chamados-ti>

---

## 1. Sobre o projeto

O Portal de Chamados de TI centraliza o atendimento de suporte tecnológico de uma instituição
pública. Servidores e colaboradores registram problemas de informática, acompanham o andamento
e interagem com a equipe de TI. Técnicos e administradores gerenciam a fila de atendimento,
atribuem responsáveis, atualizam status e acompanham indicadores de SLA.

O sistema foi construído como uma aplicação SPA (Single Page Application) em Angular consumindo
uma API REST em Spring Boot. A API concentra as regras de negócio, a persistência e a geração de
protocolos e prazos de SLA; o frontend cuida da navegação, dos formulários, dos filtros e da
apresentação dos indicadores.

### Objetivos

- Reduzir a dispersão de solicitações de TI por e-mail, telefone e mensagens informais.
- Dar visibilidade ao solicitante sobre o status e o histórico do atendimento.
- Permitir priorização, atribuição de responsável e registro de solução pela equipe técnica.
- Produzir indicadores de volume, resolução e cumprimento de SLA.
- Demonstrar a integração completa entre uma API REST Java e uma SPA Angular.

### Escopo acadêmico

A autenticação é simulada por e-mail institucional, sem senha real ou token. O projeto foi
desenvolvido para demonstração e avaliação acadêmica; as limitações e os caminhos de evolução
estão documentados na seção 16.

---

## 2. Contexto acadêmico e equipe

| Integrante | Função no projeto |
| --- | --- |
| Ericha Tainá | Frontend Angular, integração com a API e documentação |
| Kennedy Veras | Backend Spring Boot, modelagem de dados e testes |

- **Instituição:** Faculdade Senac Pernambuco
- **Curso:** Análise e Desenvolvimento de Sistemas
- **Turma:** 185 — Equipe 4
- **Proposta:** aplicação web para gestão de chamados de suporte de TI em uma instituição pública

---

## 3. Funcionalidades

### 3.1 Visão geral

- Login simplificado por e-mail institucional, com atalhos para contas de demonstração.
- Sessão do usuário mantida em `sessionStorage` e encerrada pelo botão de logout.
- Navegação adaptada ao perfil do usuário (solicitante, técnico ou administrador).
- Dashboard com totais por status, prioridade e categoria, taxa de resolução e violações de SLA.
- Listagem de chamados com filtros combináveis por texto, status, prioridade e categoria.
- Cadastro, edição, consulta e exclusão de chamados.
- Atribuição de técnico responsável.
- Atualização de status com registro de solução.
- Histórico de comentários e interações em cada chamado.
- Cálculo automático de prazo de SLA conforme a prioridade.
- Geração automática de protocolo no formato `TI-ANO-ID` (ex.: `TI-2026-0001`).
- Interface responsiva para desktop, tablet e celular.
- Estados de carregamento, erro e lista vazia nas principais telas.

### 3.2 Funcionalidades por perfil

| Perfil | Principais recursos |
| --- | --- |
| Solicitante | Abre chamados, acompanha apenas os próprios chamados, consulta o histórico e adiciona comentários. Não acessa o dashboard, a fila geral, a edição ou a exclusão. |
| Técnico | Consulta a fila geral, filtra chamados, abre o detalhe, edita dados, atribui responsável, atualiza status, registra solução e comenta. Não acessa o dashboard e não vê o atalho de abertura de chamado. |
| Administrador | Possui os recursos do técnico e também acessa o dashboard, o atalho de abertura de chamado e a exclusão de chamados. |

> O controle de navegação e de ações por perfil é aplicado no frontend. O backend não possui
> autenticação real nem autorização por perfil nesta entrega; consulte a seção 16.

### 3.3 Dashboard

O dashboard é exclusivo do perfil `ADMINISTRADOR` e apresenta:

- total de chamados;
- quantidade de chamados abertos, em andamento, resolvidos e com prioridade crítica;
- quantidade de chamados com SLA violado;
- distribuição de chamados por categoria;
- volume de chamados por prioridade;
- percentual de resolução, considerando chamados `RESOLVIDO` e `FECHADO`;
- os seis chamados mais recentes, ordenados pela data de abertura.

### 3.4 Lista e detalhe de chamados

- A lista de gestão exibe protocolo, assunto, categoria, solicitante, técnico, prioridade,
  status e data de abertura.
- A lista do solicitante exibe apenas os chamados abertos por ele.
- O detalhe apresenta descrição, solução aplicada, linha do tempo, comentários e informações
  complementares.
- O detalhe do solicitante valida que o chamado pertence ao usuário logado.
- Técnicos e administradores podem editar os dados do chamado, atualizar o atendimento e
  comentar. Apenas administradores podem excluir.
- O formulário de chamado permite escolher categoria, prioridade, local, solicitante e técnico.
  Para solicitantes, o solicitante é preenchido automaticamente e o técnico não é informado.

### 3.5 Filtros

A API aceita os filtros `status`, `prioridade`, `categoria`, `tecnicoId` e `busca`. O frontend
expõe busca, status, prioridade e categoria na tela de chamados. A busca textual considera
protocolo, título e descrição, sem diferenciar maiúsculas de minúsculas.

---

## 4. Arquitetura

```
┌──────────────────────────────┐          HTTP/JSON          ┌──────────────────────────────┐
│  Frontend Angular 22         │  ───────────────────────▶   │  Backend Spring Boot 4       │
│                              │                             │                              │
│  Pages / Components          │  ◀───────────────────────   │  Controllers                 │
│  Services (HttpClient)       │                             │  Services                    │
│  Signals / Models            │                             │  Repositories (JPA)          │
└──────────────────────────────┘                             └──────────────┬───────────────┘
                                                                            │
                                                                            ▼
                                                                   ┌──────────────────┐
                                                                   │ H2 Database      │
                                                                   │ (em memória)     │
                                                                   └──────────────────┘
```

### 4.1 Fluxo de uma requisição

1. O usuário interage com uma página Angular.
2. A página chama um método do `ApiService`.
3. O `ApiService` envia uma requisição HTTP para `http://localhost:8080`.
4. O controller recebe o DTO, valida os dados e delega ao service.
5. O service aplica as regras de negócio e acessa o repository.
6. O repository persiste ou consulta os dados no H2.
7. O service converte a entidade em DTO de resposta.
8. O frontend recebe o JSON, atualiza os signals e re-renderiza a interface.

### 4.2 Decisões estruturais

- **Backend em camadas:** controller, service, repository, model, DTO, config e exception.
- **Frontend standalone:** sem `NgModules`, com rotas, componentes standalone e signals.
- **Serviço HTTP único:** o `ApiService` centraliza a URL da API e os endpoints.
- **DTOs:** evitam expor entidades JPA e problemas de serialização de relações lazy.
- **Dados de demonstração:** o `DataInitializer` garante uma base consistente em qualquer máquina.
- **Componentização:** telas reutilizam componentes de cabeçalho, badges, filtros, tabela,
  indicadores, linha do tempo e formulário de status.

---

## 5. Tecnologias e versões

### 5.1 Backend

| Tecnologia | Versão / observação |
| --- | --- |
| Java | 17 (compilação com `release 17`; testado com JDK 21) |
| Spring Boot | 4.1.1 |
| Spring Web MVC | via `spring-boot-starter-webmvc` |
| Spring Data JPA / Hibernate | via Spring Boot 4 |
| Bean Validation | via `spring-boot-starter-validation` |
| H2 Database | 2.4.240 (runtime) |
| Maven | 3.9.16 via Maven Wrapper |
| Testes | Spring Boot Test, JUnit 5 e AssertJ |

### 5.2 Frontend

| Tecnologia | Versão / observação |
| --- | --- |
| Angular | 22.2 |
| TypeScript | 6.0 |
| Angular Material / CDK | 22.2 |
| RxJS | 7.8 |
| Node.js | 22+ (testado com Node 26) |
| npm | 10+ |
| Prettier | 3.8 |
| Vitest | 5 (builder de testes do Angular) |

### 5.3 Configuração do backend

O arquivo `backend/src/main/resources/application.properties` define:

- porta `8080`;
- banco H2 em memória em `jdbc:h2:mem:chamadosdb;DB_CLOSE_DELAY=-1`;
- console H2 em `/h2-console`;
- `ddl-auto=update`;
- `open-in-view=false`.

---

## 6. Estrutura do repositório

```
portal-chamados/
├── backend/
│   ├── mvnw / mvnw.cmd
│   ├── pom.xml
│   ├── src/main/java/br/com/chamados/
│   │   ├── ChamadosApplication.java
│   │   ├── config/          # CORS e carga de dados iniciais
│   │   ├── controller/      # AuthController, ChamadoController, UsuarioController
│   │   ├── dto/             # Contratos de entrada e saída
│   │   ├── exception/       # Exceções de domínio e tratamento global
│   │   ├── model/           # Entidades JPA
│   │   │   └── enums/       # Perfil, categoria, prioridade e status
│   │   ├── repository/      # Interfaces Spring Data JPA
│   │   └── service/         # Regras de negócio
│   │       └── mapper/      # Conversão de entidade para DTO
│   ├── src/main/resources/
│   │   └── application.properties
│   └── src/test/java/br/com/chamados/
│       ├── ChamadosApplicationTests.java
│       └── ChamadoServiceTests.java
├── frontend/
│   ├── angular.json
│   ├── package.json
│   └── src/app/
│       ├── app.config.ts
│       ├── app.routes.ts
│       ├── app.ts
│       ├── core/
│       │   ├── api.service.ts
│       │   ├── auth.guard.ts
│       │   ├── auth.service.ts
│       │   └── models.ts
│       ├── shared/
│       │   └── constants.ts
│       ├── components/
│       │   ├── shell/
│       │   ├── page-header/
│       │   ├── status-badge/
│       │   ├── priority-badge/
│       │   ├── ticket-filters/
│       │   ├── ticket-table/
│       │   ├── dashboard-stats/
│       │   ├── ticket-timeline/
│       │   └── ticket-status-form/
│       └── pages/
│           ├── login/
│           ├── dashboard/
│           ├── chamados/
│           ├── meus-chamados/
│           ├── chamado-form/
│           └── chamado-detalhe/
├── docs/
│   ├── DOCUMENTACAO.md
│   ├── BACKEND.md
│   ├── APRESENTACAO.md
│   └── DIVISAO_EQUIPE.md
└── README.md
```

---

## 7. Pré-requisitos e execução

### 7.1 Pré-requisitos

- **Java 17 ou superior** (`java -version`)
- **Node.js 22 ou superior** (`node -v`)
- **npm 10 ou superior** (`npm -v`)
- Não é necessário instalar Maven: o projeto usa **Maven Wrapper** (`mvnw`).

### 7.2 Executar o backend

```bash
cd backend

# Linux/macOS
chmod +x mvnw   # apenas se o wrapper não estiver executável
./mvnw spring-boot:run

# Alternativa no Linux/macOS, sem alterar permissões
bash mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```

A API estará disponível em `http://localhost:8080`.

- Console do H2: `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:mem:chamadosdb`
  - Usuário: `sa`
  - Senha: vazia

Os dados são recriados a cada inicialização. O `DataInitializer` cadastra 6 usuários e
10 chamados de exemplo quando o banco está vazio.

### 7.3 Executar os testes do backend

```bash
cd backend
./mvnw test
```

A suíte é composta por 9 testes: 2 de contexto e carga inicial e 7 de regras do
`ChamadoService`.

### 7.4 Executar o frontend

```bash
cd frontend
npm install
npm start
```

Acesse `http://localhost:4200`.

> O backend precisa estar em execução para que o login, as listas e os indicadores sejam
> carregados. O CORS está configurado para `http://localhost:4200`, `http://localhost:4201`,
> `http://127.0.0.1:4200` e `http://127.0.0.1:4201`.

### 7.5 Build de produção

```bash
cd frontend
npm run build
```

Os arquivos finais são gerados em `frontend/dist/portal-chamados-ti/`, com os arquivos públicos
em `browser/`.

### 7.6 Execução rápida

Em dois terminais:

```bash
# Terminal 1
cd backend
./mvnw spring-boot:run

# Terminal 2
cd frontend
npm install
npm start
```

Depois, abra `http://localhost:4200` e entre com uma das contas de demonstração.

---

## 8. Contas de demonstração

A autenticação é simulada por e-mail. O campo de senha existe na tela para representar o fluxo,
mas **não é validado**. Também é possível clicar em uma das contas listadas na tela de login.

| Nome | E-mail | Perfil | Destino após o login |
| --- | --- | --- | --- |
| Ericha Taina | `ericha@senac.pe.gov.br` | Administrador | `/dashboard` |
| Kennedy Veras | `kennedy@senac.pe.gov.br` | Técnico | `/chamados` |
| Diego Alves | `diego@senac.pe.gov.br` | Técnico | `/chamados` |
| Ana Souza | `ana@senac.pe.gov.br` | Solicitante | `/meus-chamados` |
| Bruno Lima | `bruno@senac.pe.gov.br` | Solicitante | `/meus-chamados` |
| Carla Mendes | `carla@senac.pe.gov.br` | Solicitante | `/meus-chamados` |

Usuários inativos ou e-mails não cadastrados recebem erro de autenticação. A API retorna
`404 Not Found` com a mensagem `Usuario nao encontrado ou inativo para o e-mail informado`.

### 8.1 Roteiro rápido de demonstração

1. Entre como administrador e mostre o dashboard e os indicadores.
2. Abra **Chamados**, aplique filtros e abra um chamado.
3. Mostre a linha do tempo, a solução aplicada e o formulário de atualização.
4. Saia e entre como técnico; atualize status, técnico e solução.
5. Entre como solicitante; mostre **Meus chamados** e abra um novo chamado.
6. Adicione um comentário e confirme o registro na linha do tempo.

---

## 9. Regras de negócio

### 9.1 Protocolo

O protocolo é gerado após o primeiro `INSERT`, quando o ID já existe:

```text
TI-<ano atual>-<ID com 4 dígitos>
```

Exemplo: `TI-2026-0011`. O formato `%04d` mantém quatro dígitos; IDs maiores que 9999 são
exibidos integralmente.

### 9.2 SLA

O prazo é calculado a partir da `dataAbertura` e da prioridade:

| Prioridade | Prazo de SLA |
| --- | --- |
| `CRITICA` | 4 horas |
| `ALTA` | 8 horas |
| `MEDIA` | 24 horas |
| `BAIXA` | 72 horas |

Na criação, o prazo usa o instante da abertura. Na edição (`PUT /chamados/{id}`), o prazo é
recalculado a partir da `dataAbertura` original.

O campo `slaViolado` é derivado na resposta:

```text
prazoSla < agora E status diferente de RESOLVIDO/FECHADO
```

Chamados resolvidos ou fechados não contam como SLA violado.

### 9.3 Status e data de fechamento

Fluxo funcional esperado:

```text
ABERTO → EM_ANDAMENTO → AGUARDANDO_USUARIO → RESOLVIDO → FECHADO
```

A reabertura só é permitida para chamados `RESOLVIDO` ou `FECHADO`; o status volta para
`EM_ANDAMENTO`.

- Ao entrar em `RESOLVIDO` ou `FECHADO`, `dataFechamento` é preenchida se ainda estiver vazia.
- Ao sair de um status encerrado, `dataFechamento` é limpa.
- A reabertura também limpa `dataFechamento` e preserva a solução registrada.
- `PATCH /chamados/{id}/status` exige um status e aceita, opcionalmente, `tecnicoId` e `solucao`.
- Uma solução em branco não apaga a solução anterior.
- A API aceita qualquer valor válido do enum de status; não há máquina de estados rígida que
  impeça, por exemplo, ir diretamente de `ABERTO` para `FECHADO`.

### 9.4 Dashboard

- `total`: todos os chamados cadastrados.
- `abertos`, `emAndamento`, `aguardandoUsuario`, `resolvidos` e `fechados`: contagens por status.
- `criticos`: chamados com prioridade `CRITICA`.
- `slaViolados`: chamados com prazo vencido e status diferente de `RESOLVIDO`/`FECHADO`.
- `percentualResolvidos`: `(resolvidos + fechados) / total * 100`, com uma casa decimal.
- `porCategoria` e `porPrioridade`: contagens para todos os valores dos enums, incluindo zeros.
- `recentes`: os seis chamados mais recentes por data de abertura.

### 9.5 Filtros e ordenação

- Os filtros de status, prioridade, categoria e técnico são opcionais e combináveis.
- A busca textual é opcional e pesquisa protocolo, título e descrição.
- A listagem é ordenada por `dataAbertura` decrescente.
- Não há paginação nesta entrega.

---

## 10. Modelo de dados

### 10.1 Usuario

Tabela: `usuarios`

| Campo | Tipo | Regras |
| --- | --- | --- |
| `id` | Long | Chave primária gerada. |
| `nome` | String | Obrigatório, até 120 caracteres. |
| `email` | String | Obrigatório, único, até 160 caracteres. |
| `setor` | String | Obrigatório, até 120 caracteres. |
| `perfil` | `PerfilUsuario` | `SOLICITANTE`, `TECNICO` ou `ADMINISTRADOR`. |
| `ativo` | boolean | Usuários inativos não aparecem em `GET /usuarios` nem autenticam. |

### 10.2 Chamado

Tabela: `chamados`

| Campo | Tipo | Regras |
| --- | --- | --- |
| `id` | Long | Chave primária gerada. |
| `protocolo` | String | Único, até 20 caracteres, gerado como `TI-ANO-ID`. |
| `titulo` | String | Obrigatório, até 160 caracteres. |
| `descricao` | String | Obrigatório, até 2000 caracteres. |
| `categoria` | `CategoriaChamado` | `HARDWARE`, `SOFTWARE`, `REDE`, `ACESSO_SENHA`, `IMPRESSORA`, `TELEFONIA` ou `OUTROS`. |
| `prioridade` | `PrioridadeChamado` | `BAIXA`, `MEDIA`, `ALTA` ou `CRITICA`. |
| `status` | `StatusChamado` | `ABERTO`, `EM_ANDAMENTO`, `AGUARDANDO_USUARIO`, `RESOLVIDO` ou `FECHADO`. Inicia como `ABERTO`. |
| `solicitante` | Usuario | Many-to-one obrigatório. |
| `tecnico` | Usuario | Many-to-one opcional. |
| `local` | String | Setor ou local físico, até 120 caracteres. |
| `dataAbertura` | LocalDateTime | Preenchida na criação. |
| `dataAtualizacao` | LocalDateTime | Atualizada em cada alteração. |
| `dataFechamento` | LocalDateTime | Preenchida ao resolver/fechar e limpa ao reabrir ou sair do status encerrado. |
| `prazoSla` | LocalDateTime | Data de abertura + prazo da prioridade. |
| `solucao` | String | Solução registrada no atendimento, até 2000 caracteres. |
| `comentarios` | List<Comentario> | Histórico do chamado, com exclusão em cascata. |

### 10.3 Comentario

Tabela: `comentarios`

| Campo | Tipo | Regras |
| --- | --- | --- |
| `id` | Long | Chave primária gerada. |
| `chamado` | Chamado | Many-to-one obrigatório. |
| `autor` | String | Obrigatório, até 120 caracteres. |
| `mensagem` | String | Obrigatório, até 2000 caracteres. |
| `dataComentario` | LocalDateTime | Preenchida no momento da inclusão. |

### 10.4 Relacionamentos

- Um `Usuario` pode ser solicitante de vários `Chamado`.
- Um `Usuario` técnico pode ser responsável por vários `Chamado`.
- Um `Chamado` possui vários `Comentario`.
- Os comentários são removidos em cascata quando o chamado é excluído.
- Os comentários são retornados em ordem crescente de `dataComentario`.

---

## 11. API REST

### 11.1 Informações gerais

- **Base URL local:** `http://localhost:8080`
- **Prefixo:** não há `/api`; os endpoints são `/auth`, `/usuarios` e `/chamados`.
- **Formato:** JSON.
- **Autenticação:** simulada; não há token, cookie de sessão no backend ou verificação de perfil.
- **Datas:** `LocalDateTime` em ISO 8601, sem timezone.

### 11.2 Endpoints

| Método | Endpoint | Parâmetros / corpo | Descrição | Sucesso |
| --- | --- | --- | --- | --- |
| `POST` | `/auth/login` | `{ "email": "..." }` | Autentica por e-mail e retorna o usuário ativo. | `200` |
| `GET` | `/usuarios` | - | Lista usuários ativos ordenados por nome. | `200` |
| `GET` | `/chamados` | `status`, `prioridade`, `categoria`, `tecnicoId`, `busca` (opcionais) | Lista chamados com filtros combináveis. | `200` |
| `GET` | `/chamados/{id}` | `id` no path | Retorna o chamado com comentários. | `200` |
| `POST` | `/chamados` | `ChamadoRequest` | Cria chamado, gera protocolo e SLA. | `201` |
| `PUT` | `/chamados/{id}` | `ChamadoRequest` | Atualiza os dados e recalcula o SLA. | `200` |
| `PATCH` | `/chamados/{id}/status` | `AtualizarStatusRequest` | Atualiza status, técnico e solução. | `200` |
| `PATCH` | `/chamados/{id}/reabrir` | - | Reabre chamado resolvido/fechado. | `200` |
| `POST` | `/chamados/{id}/comentarios` | `ComentarioRequest` | Adiciona comentário ao histórico. | `201` |
| `DELETE` | `/chamados/{id}` | `id` no path | Exclui o chamado e seus comentários. | `204` |
| `GET` | `/chamados/dashboard` | - | Retorna indicadores agregados. | `200` |

### 11.3 Login

```http
POST /auth/login
Content-Type: application/json

{
  "email": "kennedy@senac.pe.gov.br"
}
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

### 11.4 Listagem de chamados

```http
GET /chamados?status=ABERTO&prioridade=ALTA&categoria=HARDWARE&tecnicoId=2&busca=impressora
```

Todos os parâmetros são opcionais. A resposta é uma lista de objetos no formato abaixo:

```json
{
  "id": 11,
  "protocolo": "TI-2026-0011",
  "titulo": "Computador não liga",
  "descricao": "O equipamento não apresenta sinal de vídeo ao ser ligado.",
  "categoria": "HARDWARE",
  "prioridade": "ALTA",
  "status": "ABERTO",
  "solicitanteId": 4,
  "solicitanteNome": "Ana Souza",
  "solicitanteSetor": "Secretaria Academica",
  "tecnicoId": 2,
  "tecnicoNome": "Kennedy Veras",
  "local": "Secretaria Acadêmica - Sala 12",
  "dataAbertura": "2026-11-08T12:28:26.766385",
  "dataAtualizacao": "2026-11-08T12:28:26.766385",
  "dataFechamento": null,
  "prazoSla": "2026-11-08T20:28:26.766385",
  "slaViolado": false,
  "solucao": null,
  "comentarios": []
}
```

### 11.5 Criação de chamado

```http
POST /chamados
Content-Type: application/json

{
  "titulo": "Computador não liga",
  "descricao": "O equipamento não apresenta sinal de vídeo ao ser ligado.",
  "categoria": "HARDWARE",
  "prioridade": "ALTA",
  "solicitanteId": 4,
  "tecnicoId": 2,
  "local": "Secretaria Acadêmica - Sala 12"
}
```

Resposta `201 Created` com o chamado criado, protocolo gerado, status `ABERTO`, datas
preenchidas e `prazoSla` calculado.

### 11.6 Atualização de chamado

```http
PUT /chamados/11
Content-Type: application/json
```

O corpo é o mesmo da criação. A resposta `200 OK` retorna o chamado atualizado.

### 11.7 Atualização de status

```http
PATCH /chamados/11/status
Content-Type: application/json

{
  "status": "RESOLVIDO",
  "tecnicoId": 2,
  "solucao": "Fonte substituída e equipamento testado."
}
```

`tecnicoId` e `solucao` são opcionais. A resposta `200 OK` retorna o chamado atualizado.

### 11.8 Reabertura

```http
PATCH /chamados/11/reabrir
```

Só é permitida para chamados `RESOLVIDO` ou `FECHADO`. O status volta para `EM_ANDAMENTO` e
`dataFechamento` é limpa. Caso o chamado não esteja encerrado, a API retorna `400 Bad Request`.

### 11.9 Comentários

```http
POST /chamados/11/comentarios
Content-Type: application/json

{
  "autor": "Diego Alves",
  "mensagem": "Peça solicitada ao almoxarifado."
}
```

Resposta `201 Created`:

```json
{
  "id": 21,
  "autor": "Diego Alves",
  "mensagem": "Peça solicitada ao almoxarifado.",
  "dataComentario": "2026-11-08T13:10:00"
}
```

### 11.10 Dashboard

```http
GET /chamados/dashboard
```

Resposta `200 OK`:

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

### 11.11 Exclusão

```http
DELETE /chamados/11
```

Resposta `204 No Content`. A exclusão remove também os comentários do chamado.

### 11.12 Erros

Erros de validação retornam `400 Bad Request`:

```json
{
  "timestamp": "2026-11-08T12:00:00",
  "status": 400,
  "mensagem": "Dados invalidos",
  "campos": {
    "titulo": "O titulo e obrigatorio"
  }
}
```

Recursos inexistentes retornam `404 Not Found` com a mensagem do recurso. Regras de negócio,
como reabrir um chamado que não está encerrado, retornam `400 Bad Request`.

### 11.13 Validações de entrada

| DTO | Campo | Regra |
| --- | --- | --- |
| `LoginRequest` | `email` | Obrigatório. |
| `ChamadoRequest` | `titulo` | Obrigatório, máximo 160 caracteres. |
| `ChamadoRequest` | `descricao` | Obrigatório, máximo 2000 caracteres. |
| `ChamadoRequest` | `categoria` | Obrigatório. |
| `ChamadoRequest` | `prioridade` | Obrigatório. |
| `ChamadoRequest` | `solicitanteId` | Obrigatório. |
| `ChamadoRequest` | `tecnicoId` | Opcional. |
| `ChamadoRequest` | `local` | Opcional, máximo 120 caracteres. |
| `AtualizarStatusRequest` | `status` | Obrigatório. |
| `AtualizarStatusRequest` | `tecnicoId` | Opcional. |
| `AtualizarStatusRequest` | `solucao` | Opcional, máximo 2000 caracteres. |
| `ComentarioRequest` | `autor` | Obrigatório, máximo 120 caracteres. |
| `ComentarioRequest` | `mensagem` | Obrigatório, máximo 2000 caracteres. |

---

## 12. Frontend em detalhe

### 12.1 Inicialização

- `src/main.ts` inicializa a aplicação standalone.
- `app.config.ts` registra o roteador, o `HttpClient` e a restauração de scroll.
- `app.ts` contém apenas o `router-outlet`.
- `app.routes.ts` define as rotas públicas e internas e aplica os guards.
- `src/material-theme.scss` e `src/styles.css` concentram o tema e os estilos globais.

### 12.2 Rotas e guards

| Rota | Componente | Acesso | Observação |
| --- | --- | --- | --- |
| `/login` | `Login` | Público | Redireciona usuários já autenticados para o destino do perfil. |
| `/dashboard` | `Dashboard` | `authGuard` + `dashboardGuard` | Apenas `ADMINISTRADOR`; solicitante vai para `/meus-chamados` e técnico para `/chamados`. |
| `/meus-chamados` | `MeusChamados` | `authGuard` + `solicitanteGuard` | Apenas `SOLICITANTE`. |
| `/meus-chamados/:id` | `ChamadoDetalhe` | `authGuard` + `solicitanteGuard` | Detalhe de chamado do próprio solicitante. |
| `/chamados` | `Chamados` | `authGuard` + `staffGuard` | Técnicos e administradores. |
| `/chamados/novo` | `ChamadoForm` | `authGuard` | Atalho exibido para solicitante e administrador. |
| `/chamados/:id` | `ChamadoDetalhe` | `authGuard` | O componente valida se o solicitante é o dono do chamado. |
| `/chamados/:id/editar` | `ChamadoForm` | `authGuard` + `staffGuard` | Técnicos e administradores. |
| `**` | - | - | Redireciona para a rota raiz. |

Guards disponíveis em `core/auth.guard.ts`:

- `authGuard`: exige usuário na sessão.
- `staffGuard`: bloqueia `SOLICITANTE` e redireciona para `/meus-chamados`.
- `dashboardGuard`: permite apenas `ADMINISTRADOR`.
- `solicitanteGuard`: permite apenas `SOLICITANTE`.

### 12.3 Serviços e estado

| Arquivo | Responsabilidade |
| --- | --- |
| `core/api.service.ts` | Centraliza a URL `http://localhost:8080` e as chamadas HTTP da API. |
| `core/auth.service.ts` | Mantém o usuário atual em um signal e persiste a sessão em `sessionStorage` com a chave `portal-chamados.usuario`. |
| `core/auth.guard.ts` | Aplica as regras de acesso às rotas. |
| `core/models.ts` | Tipos TypeScript de usuário, chamado, comentário, dashboard e filtros. |
| `shared/constants.ts` | Rótulos em português, opções de formulário, classes de badge e funções de formatação de data e iniciais. |

O frontend usa **signals** para estado local, **computed** para valores derivados e
**input/output** nos componentes reutilizáveis. As datas ISO são formatadas em pt-BR com
`Intl.DateTimeFormat`, e os enums são traduzidos por mapas de rótulos.

### 12.4 Componentes reutilizáveis

| Componente | Responsabilidade |
| --- | --- |
| `Shell` | Layout autenticado com sidebar, topbar, navegação por perfil, avatar, menu mobile e logout. |
| `PageHeader` | Cabeçalho de página com eyebrow, título, subtítulo, link de volta e ações projetadas via `ng-content`. |
| `StatusBadge` | Exibe o status do chamado com rótulo e classe visual. |
| `PriorityBadge` | Exibe a prioridade do chamado com rótulo e classe visual. |
| `TicketFilters` | Formulário de filtros com busca, status, prioridade e categoria; emite eventos para filtrar e limpar. |
| `TicketTable` | Tabela compartilhada nos modos `gestao`, `solicitante` e `recentes`, com badges, links, ações e estado vazio. |
| `DashboardStats` | Cards de totais do dashboard. |
| `TicketTimeline` | Linha do tempo com abertura, comentários e encerramento do chamado. |
| `TicketStatusForm` | Formulário de status, técnico responsável e solução; mantém o formulário sincronizado com o chamado recebido. |

### 12.5 Páginas

| Página | Responsabilidade |
| --- | --- |
| `Login` | Carrega as contas de demonstração, autentica por e-mail e redireciona conforme o perfil. |
| `Dashboard` | Carrega os indicadores, mostra estatísticas, distribuições, taxa de resolução e chamados recentes. |
| `Chamados` | Lista a fila de gestão, aplica filtros, permite excluir para administradores e navega para detalhe/edição. |
| `MeusChamados` | Lista somente os chamados do solicitante logado. |
| `ChamadoForm` | Cria e edita chamados; adapta campos e comportamento para solicitante ou equipe técnica. |
| `ChamadoDetalhe` | Exibe dados, solução, linha do tempo, comentários, gestão de status e exclusão. |

### 12.6 Experiência de uso

- Layout responsivo com sidebar recolhível e overlay no mobile.
- Indicadores de carregamento, mensagens de erro e estados vazios.
- Confirmação antes de excluir um chamado.
- Feedback de sucesso e erro após atualizar status ou adicionar comentário.
- Navegação consistente entre lista, detalhe, edição e criação.
- Interface em português, com datas, rótulos e categorias localizados.

---

## 13. Backend em detalhe

### 13.1 Camadas

| Camada | Pacote | Responsabilidade |
| --- | --- | --- |
| Controllers | `br.com.chamados.controller` | Expõem os endpoints REST, recebem DTOs validados e delegam ao service. |
| Services | `br.com.chamados.service` | Concentram regras de negócio: protocolo, SLA, status, reabertura, comentários e dashboard. |
| Repositories | `br.com.chamados.repository` | Interfaces Spring Data JPA e consulta JPQL customizada de filtros. |
| Models | `br.com.chamados.model` | Entidades JPA `Usuario`, `Chamado` e `Comentario` e enums de domínio. |
| DTOs | `br.com.chamados.dto` | Contratos de entrada e saída da API, sem expor entidades. |
| Mapper | `br.com.chamados.service.mapper` | Converte `Chamado` em `ChamadoResponse`. |
| Config | `br.com.chamados.config` | CORS e carga de dados iniciais (`DataInitializer`). |
| Exception | `br.com.chamados.exception` | Exceções de domínio e tratamento global com `@RestControllerAdvice`. |

### 13.2 Controllers

- `AuthController`: `POST /auth/login`.
- `UsuarioController`: `GET /usuarios`.
- `ChamadoController`: listagem, detalhe, criação, atualização, status, reabertura, comentários,
  exclusão e dashboard.

### 13.3 Services

- `UsuarioService`: lista usuários ativos, busca entidades e autentica por e-mail.
- `ChamadoService`: implementa criação, edição, listagem, detalhe, atualização de status,
  reabertura, comentários, exclusão e dashboard.
- `ChamadoMapper`: monta o `ChamadoResponse` e calcula o campo derivado `slaViolado`.

### 13.4 Repositories

- `UsuarioRepository`: busca usuários ativos e por e-mail.
- `ChamadoRepository`: consulta com filtros combináveis, contagens do dashboard e busca dos seis
  chamados mais recentes.
- `ComentarioRepository`: interface JPA disponível para comentários.

A consulta de filtros usa uma única JPQL com parâmetros opcionais e `JOIN FETCH` para
solicitante e técnico, reduzindo consultas adicionais.

### 13.5 Tratamento de erros

`GlobalExceptionHandler` padroniza as respostas:

- `404 Not Found` para `RecursoNaoEncontradoException`.
- `400 Bad Request` para `RegraDeNegocioException`.
- `400 Bad Request` para erros de validação, com o mapa `campos`.

### 13.6 CORS

`CorsConfig` libera as origens:

- `http://localhost:4200`
- `http://localhost:4201`
- `http://127.0.0.1:4200`
- `http://127.0.0.1:4201`

Métodos permitidos: `GET`, `POST`, `PUT`, `PATCH`, `DELETE` e `OPTIONS`.

### 13.7 Dados iniciais

O `DataInitializer` é executado na inicialização e só popula o banco se não houver usuários.
Ele cria:

- 6 usuários: 1 administrador, 2 técnicos e 3 solicitantes;
- 10 chamados com diferentes categorias, prioridades e status;
- comentários e soluções em parte dos chamados;
- protocolos e prazos de SLA coerentes com a data de abertura de cada exemplo.

### 13.8 Testes do backend

`ChamadosApplicationTests` valida o carregamento do contexto e a presença dos dados iniciais.

`ChamadoServiceTests` cobre:

- criação com protocolo `TI-ANO-ID` e SLA correto para prioridades `ALTA` e `CRITICA`;
- atualização para `RESOLVIDO` preenchendo `dataFechamento` e `solucao`;
- reabertura de chamado resolvido limpando `dataFechamento`;
- bloqueio de reabertura de chamado que não está encerrado;
- inclusão de comentário e presença do comentário no detalhe;
- filtro por técnico;
- combinação do filtro por técnico com status.

Os testes usam `@SpringBootTest` com H2 em memória e `@Transactional`, revertendo os dados
criados em cada teste.

---

## 14. Testes e validação

### 14.1 Backend

```bash
cd backend
./mvnw test
```

A suíte cobre o contexto da aplicação, a carga inicial, as principais regras de negócio e os
filtros de chamados.

### 14.2 Frontend

```bash
cd frontend
npm run build
```

O build de produção valida a compilação dos componentes, templates e tipos TypeScript.

### 14.3 Validação manual

Com a aplicação em execução, foram validados:

- login por e-mail e redirecionamento por perfil;
- listagem e filtros de chamados;
- criação de chamado com protocolo automático;
- inclusão de comentário;
- atualização de status e solução;
- reabertura de chamado;
- exclusão com resposta `204`;
- dashboard com indicadores agregados;
- navegação e restrições de rota por perfil.

### 14.4 Testes não incluídos

- Não há testes unitários ou de componentes no frontend nesta entrega.
- Não há testes de controller com `MockMvc`.
- Não há testes end-to-end automatizados.

---

## 15. Principais decisões técnicas

- **DTOs** para entrada e saída, evitando expor entidades JPA e problemas de serialização lazy.
- **Camada de serviço** concentrando regras de negócio (SLA, protocolo, transições de status,
  reabertura e comentários).
- **Tratamento global de exceções** com respostas JSON padronizadas.
- **Consulta JPQL única** com parâmetros opcionais para os filtros de chamados.
- **Banco H2 em memória** para facilitar a demonstração e a correção, sem instalação externa.
- **`DataInitializer`** para garantir uma demonstração consistente em qualquer máquina.
- **Angular standalone** com signals, computed, input/output e control flow moderno (`@if`,
  `@for`) para estado reativo e templates mais legíveis.
- **Componentes reutilizáveis** para cabeçalho, badges, filtros, tabela, estatísticas, linha do
  tempo e atualização de status.
- **Autenticação simulada** por e-mail, documentada como limitação acadêmica.

---

## 16. Limitações e evoluções

### 16.1 Limitações conhecidas

- A autenticação é simulada por e-mail; não há senha, token, sessão no backend nem Spring
  Security.
- O backend não aplica autorização por perfil. As restrições de navegação e ações são feitas no
  frontend, portanto a API pode ser chamada diretamente.
- Não há paginação ou ordenação configurável nas listagens.
- A máquina de estados não é rígida: a API aceita qualquer status válido do enum.
- O banco H2 é recriado a cada execução; não há persistência entre reinicializações.
- A URL da API está fixa em `http://localhost:8080` no `ApiService`.
- Não há anexos, notificações, auditoria, relatórios exportáveis ou upload de arquivos.
- Não há testes automatizados de frontend, controller ou fluxo end-to-end.
- O CORS está limitado às origens locais de desenvolvimento.

### 16.2 Evoluções possíveis

- Autenticação real com Spring Security e JWT.
- Autorização por perfil também no backend.
- Paginação, ordenação e busca avançada.
- Banco PostgreSQL em produção com migrações Flyway ou Liquibase.
- Testes de controller com `MockMvc` e testes end-to-end com Playwright ou Cypress.
- Upload de anexos e notificações por e-mail.
- Painel de relatórios com exportação CSV/PDF.
- Auditoria de alterações e histórico completo de status.
- Configuração da URL da API por ambiente no frontend.

---

## 17. Solução de problemas

| Problema | Causa provável | Solução |
| --- | --- | --- |
| `./mvnw: Permission denied` | Wrapper sem permissão de execução. | Use `bash mvnw ...` ou execute `chmod +x mvnw` no Linux/macOS. |
| Login sem contas de demonstração | Backend não está em execução. | Inicie o backend em `http://localhost:8080`. |
| Erro de CORS no navegador | Origem diferente das permitidas. | Acesse por `http://localhost:4200` ou `http://localhost:4201`, não pelo IP da máquina. |
| `404` ao chamar `/api/...` | A API não usa prefixo `/api`. | Use `/chamados`, `/usuarios` ou `/auth/login`. |
| Dados desaparecem ao reiniciar | H2 em memória. | Isso é esperado; o seed é recriado na inicialização. |
| Porta `8080` ocupada | Outro processo usando a porta. | Encerre o processo ou altere `server.port` no `application.properties`. |
| Porta `4200` ocupada | Outra instância do Angular. | Use `npx ng serve --port 4201`; a origem já está liberada no CORS. |
| Backend compila mas não inicia | Versão de Java incompatível. | Confirme Java 17 ou superior com `java -version`. |

---

## 18. Documentação complementar

- [`docs/DOCUMENTACAO.md`](docs/DOCUMENTACAO.md) — documentação completa do projeto,
  requisitos, arquitetura, modelo de dados, contratos da API e decisões técnicas.
- [`docs/BACKEND.md`](docs/BACKEND.md) — documentação técnica da API, camadas, regras de
  negócio, endpoints, testes e limitações do backend.
- [`docs/APRESENTACAO.md`](docs/APRESENTACAO.md) — roteiro sugerido para a apresentação final.
- [`docs/DIVISAO_EQUIPE.md`](docs/DIVISAO_EQUIPE.md) — divisão de frentes, tarefas e fluxo de
  branches/commits da dupla.

---

## 19. Resumo da entrega

| Item | Situação |
| --- | --- |
| API REST Spring Boot | Implementada |
| Frontend Angular | Implementado |
| Integração frontend ↔ backend | Implementada |
| Dashboard e indicadores | Implementados |
| Filtros e busca de chamados | Implementados |
| Criação, edição, detalhe e exclusão | Implementados |
| Atualização de status, técnico e solução | Implementada |
| Comentários e linha do tempo | Implementados |
| Reabertura de chamados | Implementada |
| SLA e protocolo automáticos | Implementados |
| Dados de demonstração | Implementados |
| Testes de serviço do backend | Implementados |
| Build de produção do frontend | Validado |
| Documentação | README e documentos complementares |
