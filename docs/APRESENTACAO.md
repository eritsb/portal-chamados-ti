# Roteiro de Apresentação — Portal de Chamados de TI

Duração sugerida: 10 a 15 minutos + perguntas.

## 1. Estrutura sugerida dos slides

1. **Capa** — nome do projeto, integrantes, instituição e turma.
2. **Problema** — solicitações de TI dispersas, sem histórico e sem priorização.
3. **Proposta** — portal único para abrir, acompanhar e gerir chamados.
4. **Funcionalidades** — dashboard, chamados, filtros, comentários, status e SLA.
5. **Tecnologias** — Java 17+, Spring Boot 4, H2, Angular 22, TypeScript e Angular Material.
6. **Arquitetura** — diagrama frontend → API → banco.
7. **Modelo de dados** — Usuario, Chamado e Comentario.
8. **Integração** — endpoints REST e fluxo de uma requisição.
9. **Demonstração** — roteiro ao vivo (abaixo).
10. **Desafios e soluções** — ex.: CORS, Jackson 3 no Spring Boot 4, tratamento de datas.
11. **Conclusão e evoluções** — autenticação real, PostgreSQL, anexos, relatórios.

## 2. Roteiro da demonstração prática

### Preparação

1. Subir o backend: `cd backend && ./mvnw spring-boot:run`.
2. Subir o frontend: `cd frontend && npm start`.
3. Abrir `http://localhost:4200` e deixar as abas do dashboard e do H2 Console prontas.

### Passo a passo

1. **Login como administrador** (`ericha@senac.pe.gov.br`) usando uma conta de demonstração.
2. Mostrar o **dashboard**: totais, categorias, prioridades, taxa de resolução e SLA.
3. Abrir **Chamados** e aplicar filtros (ex.: status “Aberto” + prioridade “Crítica”).
4. Abrir um chamado e mostrar:
   - protocolo, status, prioridade e SLA;
   - descrição e histórico;
   - informações do solicitante e técnico.
5. Fazer login como **técnico** (`felipe@senac.pe.gov.br`) e:
   - alterar o status para “Em andamento”;
   - atribuir-se como responsável;
   - registrar uma solução e mudar para “Resolvido”.
6. Adicionar um **comentário** e mostrar o histórico atualizado.
7. Abrir **Novo chamado** e cadastrar uma solicitação, evidenciando:
   - categoria e prioridade;
   - seleção de solicitante/técnico;
   - protocolo gerado automaticamente.
8. Voltar ao dashboard e mostrar os indicadores atualizados.
9. (Opcional) Abrir o **H2 Console** e consultar as tabelas `CHAMADOS`, `USUARIOS` e
   `COMENTARIOS`.
10. (Opcional) Mostrar o console do navegador com as chamadas HTTP para a API.

## 3. Pontos técnicos para explicar

- **Backend em camadas:** controller recebe DTO, service aplica regra, repository persiste.
- **DTOs:** protegem as entidades e evitam problemas de serialização lazy.
- **SLA automático:** cálculo no service conforme prioridade.
- **Protocolo:** gerado após o `INSERT`, usando o ID (`TI-ANO-ID`).
- **Tratamento de erros:** `@RestControllerAdvice` com respostas JSON padronizadas.
- **CORS:** configuração explícita para a origem do Angular.
- **Angular standalone:** rotas, guard de autenticação, signals e `@if`/`@for`.
- **Serviço HTTP único:** centraliza a URL da API e os endpoints.
- **Responsividade:** sidebar recolhível e grids adaptáveis.

## 4. Divisão sugerida da fala

| Integrante | Tema |
| --- | --- |
| Ericha Tainá | Visão geral, frontend, integração, demonstração e conclusão |
| Kennedy Veras | Backend, modelo de dados, regras de negócio e testes |

> Todos os integrantes devem saber executar o projeto e explicar o fluxo completo.

## 5. Perguntas prováveis

- **Por que H2?** Simplicidade de demonstração; o projeto está preparado para trocar por
  PostgreSQL alterando a configuração e as dependências.
- **Como funciona a autenticação?** É simulada por e-mail no escopo acadêmico; a evolução é
  Spring Security + JWT.
- **Como o SLA é calculado?** Na criação e na edição, a partir da prioridade e da data de
  abertura.
- **O que acontece ao resolver um chamado?** Registra-se a data de fechamento e o chamado deixa
  de contar como SLA violado.
- **Como o frontend sabe o status?** Os enums chegam como texto e são traduzidos por mapas de
  rótulos no frontend.
- **Como os dados iniciais são carregados?** Pela classe `DataInitializer`, executada na
  inicialização quando o banco está vazio.
