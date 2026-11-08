# Divisão de trabalho — Ericha Tainá e Kennedy Veras

> **Importante:** esta divisão foi pensada para que cada integrante faça e commit apenas
> aquilo que realmente desenvolveu, revisou ou validou. Não use os comandos abaixo para
> simular uma contribuição que não existiu: a avaliação do GitHub considera autoria e
> histórico de commits, e uma contribuição artificial pode ser caracterizada como fraude
> acadêmica. O caminho correto é Kennedy executar de fato a frente de backend e registrar
> os commits com a conta dele.

## 1. Frentes de trabalho

| Frente | Responsável | Arquivos principais |
| --- | --- | --- |
| Backend Spring Boot | Kennedy Veras | `backend/**` |
| Frontend Angular | Ericha Tainá | `frontend/**` |
| Integração e documentação | Ericha Tainá | `README.md`, `docs/**` |
| Validação e testes do backend | Kennedy Veras | `backend/src/test/**`, relatório de testes |

A documentação e a apresentação podem ser revisadas pelos dois, mas os commits devem ser
feitos por quem efetivamente escreveu ou alterou cada parte.

## 2. Tarefas reais do Kennedy (backend)

Antes de commitar, Kennedy deve:

1. Ler e explicar o fluxo `Controller → Service → Repository → H2`.
2. Executar o backend e validar manualmente pelo menos:
   - `GET /usuarios`
   - `POST /auth/login`
   - `GET /chamados`
   - `POST /chamados`
   - `PATCH /chamados/{id}/status`
   - `POST /chamados/{id}/comentarios`
   - `DELETE /chamados/{id}`
3. Rodar `./mvnw test` e corrigir eventuais falhas.
4. Implementar **uma melhoria real** no backend, por exemplo:
   - endpoint para listar chamados por técnico;
   - filtro por período de abertura;
   - teste de integração para o `ChamadoController`;
   - endpoint de reabertura de chamado.
5. Escrever `docs/BACKEND.md` com:
   - arquitetura em camadas;
   - modelo de dados;
   - decisões de SLA e protocolo;
   - tabela de endpoints;
   - instruções de execução e testes.
6. Commitar somente `backend/**` e `docs/BACKEND.md`.

Sugestão de commit:

```bash
git add backend docs/BACKEND.md
git commit -m "feat(backend): implementa API de chamados com Spring Boot e testes"
```

## 3. Tarefas reais da Ericha (frontend, integração e documentação)

Antes de commitar, Ericha deve:

1. Executar o frontend e validar os fluxos:
   - login e logout;
   - dashboard;
   - filtros da lista;
   - abertura e edição de chamado;
   - atualização de status e comentários;
   - responsividade em tela menor.
2. Rodar `npm run build` e corrigir erros.
3. Manter `README.md`, `docs/DOCUMENTACAO.md` e `docs/APRESENTACAO.md` atualizados.
4. Commitar somente `frontend/**`, `README.md` e `docs/**` (exceto `docs/BACKEND.md`).

Sugestão de commit:

```bash
git add frontend README.md docs
git commit -m "feat(frontend): implementa portal Angular e documentação do projeto"
```

## 4. Fluxo de branches sugerido

Para evidenciar o trabalho de cada um sem simulação:

```bash
# Repositório principal
git init -b main
git remote add origin <URL_DO_REPOSITORIO>

# Kennedy
git checkout -b feature/backend
git add backend docs/BACKEND.md
git commit -m "feat(backend): implementa API de chamados com Spring Boot e testes"
git push -u origin feature/backend

# Ericha (a partir da main)
git checkout main
git checkout -b feature/frontend
git add frontend README.md docs
git commit -m "feat(frontend): implementa portal Angular e documentação do projeto"
git push -u origin feature/frontend
```

Depois, cada um abre um Pull Request para `main`. A branch principal do repositório deve
permanecer como `main`.

## 5. Checklist final da dupla

- [ ] `backend/` compila e os testes passam.
- [ ] `frontend/` gera build de produção.
- [ ] API e frontend estão integrados em `localhost`.
- [ ] A documentação corresponde ao projeto real.
- [ ] Cada integrante consegue explicar o fluxo completo da aplicação.
- [ ] Os commits estão nas contas corretas e refletem trabalho efetivamente realizado.
- [ ] A branch `main` está atualizada com a versão final.
