# Template PI de Projeto de Software

Projeto Spring Boot executável para adaptar durante a PI. Ele reproduz os
padrões usados em `exercicio-cursos-online`: API em camadas, validação,
PostgreSQL, exclusão lógica, testes, cobertura, Docker e CI/CD.

Procure por `TODO(PI)` antes de começar a prova. Cada ocorrência marca uma
decisão que normalmente depende do enunciado.

## Arquitetura

```text
Cliente HTTP
    |
    v
ItemController
    |
    v
ItemService
    |
    v
ItemRepository
    |
    v
PostgreSQL
```

O pacote-base é `br.insper.templatepi`. O exemplo usa o recurso `Item` e pode
ser renomeado sem alterar a separação entre as camadas.

## Contrato de exemplo

### Criar um item

`POST /itens`

```json
{
  "nome": "Item de exemplo",
  "descricao": "Descrição do item",
  "quantidade": 10
}
```

A API retorna `201 Created`. Nome e descrição são obrigatórios e a quantidade
deve ser positiva. Os textos são salvos sem espaços no início e no fim.

### Listar itens

`GET /itens` lista os itens ativos em ordem alfabética.

`GET /itens?nome=ite` filtra pelo início do nome, sem diferenciar maiúsculas e
minúsculas.

### Excluir um item

`DELETE /itens/{id}` faz exclusão lógica e retorna `204 No Content`. Um ID
inexistente ou já excluído retorna `404 Not Found`.

## Como executar os testes

O teste unitário não precisa de Docker:

```powershell
.\mvnw.cmd -Dtest=ItemServiceTest test
```

A validação completa usa Testcontainers e exige o Docker Desktop ativo:

```powershell
.\mvnw.cmd clean verify
```

O relatório de cobertura fica em `target/site/jacoco/index.html`. O build exige
100% de linhas e branches em `ItemService`.

No Linux ou no GitHub Actions, substitua `.\mvnw.cmd` por `./mvnw`.

## Como executar com Docker Compose

Primeiro gere o JAR:

```powershell
.\mvnw.cmd clean package -DskipTests
```

Depois copie o arquivo de exemplo e preencha somente valores locais:

```powershell
Copy-Item .env.example .env
```

Para usar uma imagem construída localmente:

```powershell
docker build -t seu_usuario/template-pi-api:latest .
docker compose up -d
```

O PostgreSQL usa um volume persistente e só libera a API depois de passar no
healthcheck. O arquivo `.env` e chaves `*.pem` são ignorados pelo Git.

## Adaptação para a PI

Use esta ordem para reduzir erros de renomeação:

1. Leia o contrato e identifique recurso, campos, validações, filtros e status.
2. Renomeie `Item`, os DTOs, o repositório, o serviço e o controller.
3. Ajuste o pacote-base e os metadados do `pom.xml`, se necessário.
4. Atualize entidade, tabela, validações e consultas derivadas do repositório.
5. Atualize as rotas e os códigos HTTP.
6. Troque os cenários dos testes antes de alterar regras mais complexas.
7. Atualize a classe monitorada pelo JaCoCo no `pom.xml`.
8. Ajuste `Dockerfile`, Compose, nome da imagem e rota do healthcheck.
9. Execute `rg "TODO\(PI\)"` e resolva cada ocorrência.
10. Rode `clean verify` antes de abrir o Pull Request.

Não apague a proteção contra registros deletados sem conferir o enunciado. No
exemplo, todas as consultas públicas incluem `deletado = false`.

## CI/CD

O workflow `Testes` executa `clean verify` em Pull Requests e publica o relatório
JaCoCo como artefato.

O workflow `Publicação e deploy` executa em pushes para `main`. Ele só inicia
quando a variável `DOCKERHUB_USERNAME` está configurada. O fluxo:

1. executa testes e cobertura;
2. publica `template-pi-api:latest` e `template-pi-api:<SHA>` no Docker Hub;
3. conecta na EC2 por SSH;
4. atualiza o Compose em `/opt/template-pi`;
5. testa `GET /itens` antes de concluir.

Configure no GitHub:

| Tipo | Nome | Uso |
|---|---|---|
| Variable | `DOCKERHUB_USERNAME` | Usuário e namespace da imagem |
| Secret | `DOCKERHUB_TOKEN` | Token de publicação no Docker Hub |
| Secret | `AWS_HOST` | IP ou DNS da EC2 |
| Secret | `AWS_USER` | Usuário SSH |
| Secret | `AWS_SSH_KEY` | Chave privada de deploy |
| Secret | `AWS_KNOWN_HOSTS` | Chave pública validada do host SSH |

Na EC2, mantenha `compose.yaml` e `.env` em `/opt/template-pi`. Use no `.env`:

```dotenv
DOCKER_IMAGE=seu_usuario/template-pi-api
```

Não coloque tokens, senhas, chaves ou endereços reais no repositório.

## Checklist antes da entrega

- [ ] Todos os `TODO(PI)` foram revisados.
- [ ] Os nomes refletem o domínio da prova.
- [ ] O contrato HTTP corresponde ao enunciado.
- [ ] Registros excluídos não aparecem nas consultas.
- [ ] Testes unitários cobrem todas as decisões do serviço.
- [ ] Testes de integração usam banco descartável.
- [ ] `clean verify` termina com sucesso.
- [ ] Nenhum segredo foi versionado.
- [ ] O Pull Request mostra o pipeline aprovado.
