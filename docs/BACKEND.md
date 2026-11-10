# Backend - Portal de Chamados de TI

Documentação técnica da API REST do Portal de Chamados de TI, mantida pela frente de backend
(Kennedy Veras). O objetivo é registrar como a API funciona, quais regras de negócio ela
implementa e o que foi entregue nesta etapa.

## 1. Visão geral

O backend é uma API REST em Spring Boot que atende o frontend Angular do portal. Ele recebe
requisições HTTP/JSON, aplica as regras de negócio, persiste os dados em um banco H2 em
memória e devolve DTOs de resposta.

```
Frontend Angular
      |
      | HTTP/JSON (localhost:4200 -> localhost:8080)
      v
ChamadoController / UsuarioController / AuthController
      |
      v
ChamadoService / UsuarioService
      |
      v
ChamadoRepository / UsuarioRepository / ComentarioRepository (Spring Data JPA)
      |
      v
H2 (jdbc:h2:mem:chamadosdb)
```

### Camadas

| Camada | Pacote | Responsabilidade |
| --- | --- | --- |
| Controllers | `br.com.chamados.controller` | Expõem os endpoints REST, recebem DTOs validados e delegam ao service. |
| Services | `br.com.chamados.service` | Concentram regras de negócio: protocolo, SLA, status, reabertura e comentários. |
| Repositories | `br.com.chamados.repository` | Interfaces Spring Data JPA e consulta JPQL customizada de filtros. |
| Models | `br.com.chamados.model` | Entidades JPA `Usuario`, `Chamado`, `Comentario` e enums de domínio. |
| DTOs | `br.com.chamados.dto` | Contratos de entrada e saída da API, sem expor as entidades. |
| Mapper | `br.com.chamados.service.mapper` | Converte `Chamado` em `ChamadoResponse`. |
| Config | `br.com.chamados.config` | CORS e carga de dados iniciais (`DataInitializer`). |
| Exception | `br.com.chamados.exception` | Exceções de domínio e tratamento global com `@RestControllerAdvice`. |

### Estrutura de pastas

```
backend/
  pom.xml
  mvnw / mvnw.cmd
  src/main/java/br/com/chamados/
    ChamadosApplication.java
    config/ (CorsConfig, DataInitializer)
    controller/ (AuthController, ChamadoController, UsuarioController)
    dto/
    exception/ (GlobalExceptionHandler, RecursoNaoEncontradoException, RegraDeNegocioException)
    model/ (Chamado, Comentario, Usuario, enums)
    repository/
    service/ (ChamadoService, UsuarioService, mapper/ChamadoMapper)
  src/main/resources/application.properties
  src/test/java/br/com/chamados/
    ChamadosApplicationTests.java
    ChamadoServiceTests.java
```

## 2. Tecnologias e versões

| Item | Versão / configuração |
| --- | --- |
| Java | 17 (compilação com `release 17`); a máquina de desenvolvimento usa JDK 17 |
| Spring Boot | 4.1.1 |
| Spring Web MVC | via `spring-boot-starter-webmvc` |
| Spring Data JPA / Hibernate | Hibernate ORM 7.4.5.Final |
| Bean Validation | via `spring-boot-starter-validation` |
| Banco | H2 2.4.240, em memória |
| Build | Maven 3.9.16 via Maven Wrapper 3.3.4 |
| Testes | Spring Boot Test + JUnit 5 + AssertJ |
| Frontend integrado | Angular 22 consumindo `http://localhost:8080` |

Configuração principal em `src/main/resources/application.properties`:

- porta `8080`;
- `jdbc:h2:mem:chamadosdb;DB_CLOSE_DELAY=-1`;
- console H2 em `/h2-console`;
- `ddl-auto=update`;
- `open-in-view=false`.

## 3. Modelo de dados

### 3.1 Usuario

| Campo | Tipo | Regras |
| --- | --- | --- |
| `id` | Long | Chave primária gerada. |
| `nome` | String | Obrigatório, até 120 caracteres. |
| `email` | String | Obrigatório, único, até 160 caracteres. |
| `setor` | String | Obrigatório, até 120 caracteres. |
| `perfil` | `PerfilUsuario` | `SOLICITANTE`, `TECNICO` ou `ADMINISTRADOR`. |
| `ativo` | boolean | Usuários inativos não aparecem em `GET /usuarios` nem autenticam. |

### 3.2 Chamado

| Campo | Tipo | Regras |
| --- | --- | --- |
| `id` | Long | Chave primária gerada. |
| `protocolo` | String | Único, no formato `TI-ANO-ID`. |
| `titulo` | String | Obrigatório, até 160 caracteres. |
| `descricao` | String | Obrigatório, até 2000 caracteres. |
| `categoria` | `CategoriaChamado` | `HARDWARE`, `SOFTWARE`, `REDE`, `ACESSO_SENHA`, `IMPRESSORA`, `TELEFONIA`, `OUTROS`. |
| `prioridade` | `PrioridadeChamado` | `BAIXA`, `MEDIA`, `ALTA`, `CRITICA`. |
| `status` | `StatusChamado` | `ABERTO`, `EM_ANDAMENTO`, `AGUARDANDO_USUARIO`, `RESOLVIDO`, `FECHADO`. |
| `solicitante` | Usuario | Many-to-one obrigatório. |
| `tecnico` | Usuario | Many-to-one opcional. |
| `local` | String | Setor ou local físico, até 120 caracteres. |
| `dataAbertura` | LocalDateTime | Preenchida na criação. |
| `dataAtualizacao` | LocalDateTime | Atualizada em cada alteração. |
| `dataFechamento` | LocalDateTime | Preenchida ao resolver/fechar e limpa ao reabrir. |
| `prazoSla` | LocalDateTime | Data de abertura + prazo da prioridade. |
| `solucao` | String | Solução registrada no atendimento, até 2000 caracteres. |
| `comentarios` | List<Comentario> | Histórico do chamado, com exclusão em cascata. |

### 3.3 Comentario

| Campo | Tipo | Regras |
| --- | --- | --- |
| `id` | Long | Chave primária gerada. |
| `chamado` | Chamado | Many-to-one obrigatório. |
| `autor` | String | Obrigatório, até 120 caracteres. |
| `mensagem` | String | Obrigatório, até 2000 caracteres. |
| `dataComentario` | LocalDateTime | Preenchida no momento da inclusão. |

### Relacionamentos

- Um usuário pode ser solicitante de vários chamados.
- Um usuário técnico pode ser responsável por vários chamados.
- Um chamado possui vários comentários, removidos em cascata com o chamado.

## 4. Regras de negócio

### 4.1 Protocolo

O protocolo é gerado depois do primeiro `INSERT`, quando o ID já existe:
`TI-<ano atual>-<ID com 4 dígitos>`. Exemplo: `TI-2026-0011`. O `%04d` mantém quatro dígitos,
mas IDs maiores que 9999 continuam sendo exibidos integralmente.

### 4.2 SLA

O prazo é calculado a partir da `dataAbertura` e da prioridade:

| Prioridade | Prazo |
| --- | --- |
| `CRITICA` | 4 horas |
| `ALTA` | 8 horas |
| `MEDIA` | 24 horas |
| `BAIXA` | 72 horas |

Na criação, o prazo usa o instante da abertura. Na edição (`PUT /chamados/{id}`), o prazo é
recalculado a partir da `dataAbertura` original. O campo `slaViolado` da resposta é derivado:
`prazoSla < agora` e status diferente de `RESOLVIDO`/`FECHADO`.

### 4.3 Status e data de fechamento

- Ao entrar em `RESOLVIDO` ou `FECHADO`, `dataFechamento` é preenchida se ainda estiver vazia.
- Ao sair de um status encerrado, `dataFechamento` é limpa.
- `PATCH /chamados/{id}/status` exige um status e aceita, opcionalmente, `tecnicoId` e
  `solucao`.
- A reabertura (`PATCH /chamados/{id}/reabrir`) só é permitida para chamados `RESOLVIDO` ou
  `FECHADO`; o status volta para `EM_ANDAMENTO` e `dataFechamento` é limpa.

### 4.4 Perfis

Os perfis `SOLICITANTE`, `TECNICO` e `ADMINISTRADOR` fazem parte do modelo e são usados pelo
frontend para montar a navegação. A autenticação é simplificada por e-mail (sem senha/token) e
o backend não bloqueia operações por perfil nesta etapa.

### 4.5 DTOs, validação e erros

- A API recebe `ChamadoRequest`, `AtualizarStatusRequest`, `ComentarioRequest` e
  `LoginRequest`, todos validados com Bean Validation.
- As respostas usam `ChamadoResponse`, `UsuarioResponse`, `ComentarioResponse`,
  `DashboardResponse` e `ContagemResponse`, evitando serializar entidades e relações lazy.
- `GlobalExceptionHandler` padroniza as respostas:
  - `404 Not Found` para `RecursoNaoEncontradoException`;
  - `400 Bad Request` para `RegraDeNegocioException`;
  - `400 Bad Request` para erros de validação, com o mapa `campos`.

Formato de erro:

```json
{
  "timestamp": "2026-11-08T12:00:00",
  "status": 400,
  "mensagem": "Dados invalidos",
  "campos": { "titulo": "O titulo e obrigatorio" }
}
```

### 4.6 CORS

`CorsConfig` libera as origens `http://localhost:4200`, `http://localhost:4201`,
`http://127.0.0.1:4200` e `http://127.0.0.1:4201`, com os métodos `GET`, `POST`, `PUT`,
`PATCH`, `DELETE` e `OPTIONS`.

## 5. Endpoints

Base URL local: `http://localhost:8080`

| Método | Endpoint | Parâmetros / corpo | Descrição |
| --- | --- | --- | --- |
| `POST` | `/auth/login` | `{ "email": "..." }` | Autentica por e-mail e retorna o usuário ativo. |
| `GET` | `/usuarios` | - | Lista usuários ativos ordenados por nome. |
| `GET` | `/chamados` | `status`, `prioridade`, `categoria`, `tecnicoId`, `busca` (todos opcionais) | Lista chamados com filtros combináveis. |
| `GET` | `/chamados/{id}` | `id` no path | Retorna o chamado com comentários. |
| `POST` | `/chamados` | `ChamadoRequest` | Cria chamado, gera protocolo e SLA. |
| `PUT` | `/chamados/{id}` | `ChamadoRequest` | Atualiza os dados do chamado e recalcula o SLA. |
| `PATCH` | `/chamados/{id}/status` | `AtualizarStatusRequest` | Atualiza status, técnico e solução. |
| `PATCH` | `/chamados/{id}/reabrir` | - | Reabre chamado resolvido/fechado. |
| `POST` | `/chamados/{id}/comentarios` | `ComentarioRequest` | Adiciona comentário ao histórico. |
| `DELETE` | `/chamados/{id}` | `id` no path | Exclui o chamado e seus comentários (`204`). |
| `GET` | `/chamados/dashboard` | - | Retorna indicadores agregados. |

### Exemplos

Listagem com filtro por técnico e status:

```bash
curl "http://localhost:8080/chamados?tecnicoId=2&status=RESOLVIDO"
```

Criação:

```bash
curl -X POST http://localhost:8080/chamados \
  -H "Content-Type: application/json" \
  -d '{
    "titulo": "Computador nao liga",
    "descricao": "Equipamento sem sinal de video.",
    "categoria": "HARDWARE",
    "prioridade": "ALTA",
    "solicitanteId": 4,
    "tecnicoId": 2,
    "local": "Secretaria Academica - Sala 12"
  }'
```

Atualização de status:

```bash
curl -X PATCH http://localhost:8080/chamados/11/status \
  -H "Content-Type: application/json" \
  -d '{"status":"RESOLVIDO","tecnicoId":2,"solucao":"Fonte substituida."}'
```

Reabertura:

```bash
curl -X PATCH http://localhost:8080/chamados/11/reabrir
```

Comentário:

```bash
curl -X POST http://localhost:8080/chamados/11/comentarios \
  -H "Content-Type: application/json" \
  -d '{"autor":"Kennedy Veras","mensagem":"Equipamento em analise."}'
```

## 6. Melhorias implementadas nesta entrega

Esta etapa implementou as três melhorias previstas para a frente de backend.

### 6.1 Filtro por técnico em `GET /chamados` (Opção A)

- `ChamadoController` passou a aceitar o parâmetro opcional `tecnicoId`.
- `ChamadoService.listar` repassa o filtro ao repository.
- `ChamadoRepository.buscar` ganhou a condição
  `(:tecnicoId IS NULL OR c.tecnico.id = :tecnicoId)`.
- O filtro combina com `status`, `prioridade`, `categoria` e `busca`, mantendo compatibilidade
  com os parâmetros existentes.

Exemplo: `GET /chamados?tecnicoId=2&status=RESOLVIDO`.

### 6.2 Endpoint de reabertura (Opção C)

- Novo endpoint `PATCH /chamados/{id}/reabrir`.
- `ChamadoService.reabrir` valida se o chamado está `RESOLVIDO` ou `FECHADO`, altera o status
  para `EM_ANDAMENTO` e limpa `dataFechamento`.
- Chamados em outros status retornam `400 Bad Request` com a mensagem
  `Apenas chamados resolvidos ou fechados podem ser reabertos`.
- A solução registrada é preservada como histórico.

### 6.3 Testes de serviço (Opção B)

O arquivo `src/test/java/br/com/chamados/ChamadoServiceTests.java` cobre:

- criação com protocolo `TI-ANO-ID` e SLA correto para prioridades `ALTA` e `CRITICA`;
- atualização para `RESOLVIDO` preenchendo `dataFechamento` e `solucao`;
- reabertura de chamado resolvido limpando `dataFechamento`;
- bloqueio de reabertura de chamado que não está encerrado;
- inclusão de comentário e presença do comentário no detalhe do chamado;
- filtro por técnico;
- combinação do filtro por técnico com status.

Os testes usam `@SpringBootTest` com o H2 em memória e `@Transactional`, garantindo que os
dados criados em cada teste sejam revertidos.

## 7. Como executar

Pré-requisito: Java 17 ou superior. No Windows, o JDK 17 pode estar em `JAVA_HOME` mesmo que o
`java` do PATH seja outro; o Maven Wrapper usa `JAVA_HOME`.

```bash
cd backend

# Linux/macOS
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```

- API: `http://localhost:8080`
- H2 Console: `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:mem:chamadosdb`
  - Usuário: `sa`
  - Senha: vazia

Para compilar e rodar os testes:

```bash
cd backend

# Linux/macOS
./mvnw test

# Windows
mvnw.cmd test
```

## 8. Validação manual realizada

Com a aplicação em execução, foram validados:

- `GET /usuarios` -> 6 usuários;
- `GET /chamados` -> 10 chamados iniciais;
- `GET /chamados/dashboard` -> total 10, abertos 3, resolvidos 2;
- `POST /auth/login` com `kennedy@senac.pe.gov.br` -> usuário id 2, perfil `TECNICO`;
- `POST /chamados` -> protocolo `TI-2026-0011`, status `ABERTO` e SLA coerente;
- `POST /chamados/{id}/comentarios` -> comentário criado;
- `PATCH /chamados/{id}/status` para `RESOLVIDO` -> `dataFechamento` preenchida;
- `PATCH /chamados/{id}/reabrir` -> status `EM_ANDAMENTO` e `dataFechamento` vazia;
- `GET /chamados?tecnicoId=2` -> somente chamados do técnico 2;
- `DELETE /chamados/{id}` -> `204` e posterior `GET` retornando `404`.

Resultado dos testes automatizados: `Tests run: 9, Failures: 0, Errors: 0, Skipped: 0`.

## 9. Decisões técnicas e limitações

### Decisões

- Regras de negócio ficam no `ChamadoService`, mantendo controllers finos.
- A consulta de filtros usa uma única JPQL com parâmetros opcionais, evitando várias
  combinações de métodos no repository.
- DTOs são usados em todas as respostas para não expor entidades e relações lazy.
- O protocolo é gerado em uma segunda gravação porque depende do ID autogerado.
- `DataInitializer` popula 6 usuários e 10 chamados quando o banco está vazio, garantindo uma
  demonstração consistente.
- O H2 em memória simplifica a execução e a avaliação acadêmica.

### Limitações conhecidas

- A autenticação é simulada por e-mail, sem senha ou token; a evolução natural é Spring
  Security com JWT.
- O backend não aplica autorização por perfil; essa restrição é feita na interface.
- Não há paginação nos endpoints de listagem.
- O banco é recriado a cada inicialização; para persistência seria necessário PostgreSQL (ou
  outro banco) com migrações Flyway/Liquibase.
- Não há anexos, notificações, auditoria nem relatórios exportáveis nesta etapa.
- Não há testes de controller com `MockMvc` nesta entrega; os testes atuais cobrem o service e
  o contexto da aplicação.
