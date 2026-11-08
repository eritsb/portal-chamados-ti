# Portal de Chamados de TI

Sistema web para abertura, acompanhamento e gestão de chamados de suporte de TI em uma
instituição pública. O projeto integra um backend em **Java + Spring Boot** a um frontend em
**Angular**, com persistência em **H2** e dados de demonstração pré-carregados.

> Projeto Final — Residência Porto Digital / SENAC 3º Período — CAPGEMINI

---

## 1. Identificação da equipe

| Integrante | Função no projeto |
| --- | --- |
| Ericha Tainá | Frontend Angular, integração com a API e documentação |
| Kennedy Veras | Backend Spring Boot, modelagem de dados e testes |

**Instituição:** Faculdade Senac Pernambuco — Análise e Desenvolvimento de Sistemas
**Turma:** 185 — Equipe 4

---

## 2. Descrição do projeto

O Portal de Chamados de TI centraliza o atendimento de suporte tecnológico de uma instituição
pública. Servidores e colaboradores registram problemas de informática, acompanham o andamento
e interagem com a equipe de TI. Técnicos e administradores gerenciam a fila de atendimento,
atribuem responsáveis, atualizam status e acompanham indicadores de SLA.

### Funcionalidades implementadas

- Login simplificado por e-mail institucional (contas de demonstração).
- Dashboard com totais por status, prioridade, categoria, taxa de resolução e violações de SLA.
- Cadastro, edição, consulta e exclusão de chamados.
- Filtros por texto, status, prioridade e categoria.
- Atribuição de técnico responsável.
- Atualização de status com registro de solução.
- Histórico de comentários/interações em cada chamado.
- Cálculo automático de prazo de SLA conforme a prioridade.
- Geração automática de protocolo (ex.: `TI-2026-0001`).
- Perfis de acesso: solicitante, técnico e administrador.
- Interface responsiva para desktop, tablet e celular.

### Regras de negócio

| Prioridade | Prazo de SLA |
| --- | --- |
| Crítica | 4 horas |
| Alta | 8 horas |
| Média | 24 horas |
| Baixa | 72 horas |

Fluxo de status: `ABERTO` → `EM_ANDAMENTO` → `AGUARDANDO_USUARIO` → `RESOLVIDO` → `FECHADO`.
Ao marcar como resolvido ou fechado, a data de fechamento é registrada. Chamados encerrados não
contam como SLA violado.

---

## 3. Tecnologias e versões

### Backend

| Tecnologia | Versão |
| --- | --- |
| Java | 17+ (testado com JDK 21) |
| Spring Boot | 4.1.1 |
| Spring Web MVC | 7.x (via Spring Boot) |
| Spring Data JPA / Hibernate | via Spring Boot 4 |
| Bean Validation | via Spring Boot Starter Validation |
| H2 Database | 2.4.240 (runtime) |
| Maven | 3.9.16 (wrapper incluso) |

### Frontend

| Tecnologia | Versão |
| --- | --- |
| Angular | 22.2 |
| TypeScript | 6.0 |
| Angular Material | 22.2 |
| RxJS | 7.8 |
| Node.js | 22+ (testado com Node 26) |
| npm | 12 |

---

## 4. Estrutura do repositório

```
portal-chamados/
├── backend/                 # API REST Spring Boot
│   ├── src/main/java/br/com/chamados/
│   │   ├── config/          # CORS e carga de dados iniciais
│   │   ├── controller/      # Endpoints REST
│   │   ├── dto/             # Contratos de entrada e saída
│   │   ├── exception/       # Tratamento global de erros
│   │   ├── model/           # Entidades e enums
│   │   ├── repository/      # Acesso ao banco (JPA)
│   │   └── service/         # Regras de negócio
│   └── src/main/resources/application.properties
├── frontend/                # Aplicação Angular
│   └── src/app/
│       ├── core/            # Modelos, serviços HTTP, autenticação e guard
│       ├── shared/          # Constantes, rótulos e utilitários
│       ├── components/      # Layout autenticado (shell)
│       └── pages/           # Login, dashboard, chamados, formulário e detalhe
├── docs/                    # Documentação complementar e roteiro de apresentação
└── README.md
```

---

## 5. Pré-requisitos

- **Java 17 ou superior** (`java -version`)
- **Node.js 22 ou superior** (`node -v`)
- **npm 10 ou superior** (`npm -v`)
- Não é necessário instalar Maven: o projeto usa **Maven Wrapper** (`mvnw`).

---

## 6. Como executar o backend

```bash
cd backend

# Linux/macOS
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```

A API estará disponível em `http://localhost:8080`.

- Console do H2: `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:mem:chamadosdb`
  - Usuário: `sa`
  - Senha: (vazia)
- Os dados são recriados a cada inicialização (banco em memória) por meio da classe
  `DataInitializer`, que cadastra 6 usuários e 10 chamados de exemplo.

### Executar os testes do backend

```bash
cd backend
./mvnw test
```

---

## 7. Como executar o frontend

```bash
cd frontend
npm install
npm start
```

Acesse `http://localhost:4200`.

> O backend precisa estar em execução para que o login e os dados sejam carregados.
> O CORS está configurado para `http://localhost:4200` e `http://localhost:4201`.

### Build de produção

```bash
cd frontend
npm run build
```

Os arquivos finais são gerados em `frontend/dist/portal-chamados-ti`.

---

## 8. Contas de demonstração

A autenticação é **simulada por e-mail** (sem senha real), adequada ao escopo acadêmico do
projeto. Clique em uma das contas na tela de login ou informe o e-mail:

| Nome | E-mail | Perfil |
| --- | --- | --- |
| Ericha Taina | `ericha@senac.pe.gov.br` | Administrador |
| Kennedy Veras | `kennedy@senac.pe.gov.br` | Técnico |
| Diego Alves | `diego@senac.pe.gov.br` | Técnico |
| Ana Souza | `ana@senac.pe.gov.br` | Solicitante |
| Bruno Lima | `bruno@senac.pe.gov.br` | Solicitante |
| Carla Mendes | `carla@senac.pe.gov.br` | Solicitante |

Perfis **técnico** e **administrador** podem atualizar status e responsável. Apenas o
**administrador** pode excluir chamados.

---

## 9. Integração frontend ↔ backend

O frontend consome a API via `HttpClient` no serviço `frontend/src/app/core/api.service.ts`.
A URL base é `http://localhost:8080`. O backend libera CORS para a origem do Angular. As datas
são trafegadas em ISO 8601 e formatadas no frontend com `Intl.DateTimeFormat('pt-BR')`.

### Endpoints utilizados

| Método | Endpoint | Descrição |
| --- | --- | --- |
| `POST` | `/auth/login` | Autentica pelo e-mail e retorna o usuário |
| `GET` | `/usuarios` | Lista usuários ativos (login, solicitantes e técnicos) |
| `GET` | `/chamados` | Lista chamados; aceita `status`, `prioridade`, `categoria` e `busca` |
| `GET` | `/chamados/{id}` | Detalha um chamado com histórico de comentários |
| `POST` | `/chamados` | Cria um chamado |
| `PUT` | `/chamados/{id}` | Atualiza os dados do chamado |
| `PATCH` | `/chamados/{id}/status` | Atualiza status, técnico e solução |
| `POST` | `/chamados/{id}/comentarios` | Adiciona comentário ao histórico |
| `DELETE` | `/chamados/{id}` | Exclui um chamado |
| `GET` | `/chamados/dashboard` | Retorna indicadores agregados |

### Exemplo de criação de chamado

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

### Exemplo de resposta

```json
{
  "id": 11,
  "protocolo": "TI-2026-0011",
  "titulo": "Computador não liga",
  "status": "ABERTO",
  "prioridade": "ALTA",
  "solicitanteId": 4,
  "solicitanteNome": "Ana Souza",
  "tecnicoId": 2,
  "tecnicoNome": "Kennedy Veras",
  "dataAbertura": "2026-11-08T12:28:26.766385",
  "prazoSla": "2026-11-08T20:28:26.766385",
  "slaViolado": false,
  "comentarios": []
}
```

---

## 10. Documentação complementar

- [`docs/DOCUMENTACAO.md`](docs/DOCUMENTACAO.md) — documentação completa do projeto,
  arquitetura, modelo de dados, contratos da API e decisões técnicas.
- [`docs/APRESENTACAO.md`](docs/APRESENTACAO.md) — roteiro sugerido para a apresentação final.
- [`docs/DIVISAO_EQUIPE.md`](docs/DIVISAO_EQUIPE.md) — divisão de frentes, tarefas e fluxo de
  branches/commits da dupla.

---

## 11. Principais decisões técnicas

- **DTOs** para entrada e saída, evitando expor entidades JPA e problemas de serialização lazy.
- **Camada de serviço** concentrando regras de negócio (SLA, protocolo, transições de status).
- **Tratamento global de exceções** com respostas JSON padronizadas.
- **Banco H2 em memória** para facilitar a demonstração e a correção, sem instalação externa.
- **Signals e control flow moderno do Angular** (`@if`, `@for`) para estado reativo e templates
  mais legíveis.
- **Autenticação simulada** por e-mail, documentada como limitação acadêmica; em produção seria
  substituída por Spring Security/JWT.

---

## 12. Possíveis evoluções

- Autenticação real com Spring Security e JWT.
- Perfis e permissões aplicados também no backend.
- Paginação e ordenação da lista de chamados.
- Upload de anexos e notificações por e-mail.
- Painel de relatórios com exportação CSV/PDF.
- Banco PostgreSQL em produção e migrações com Flyway.
