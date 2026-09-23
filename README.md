# Template PI de Projeto de Software

API Spring Boot para adaptar durante a PI. O projeto inclui PostgreSQL, testes,
Docker, CI/CD e exemplos de Strategy, Factory e Observer.

## Arquitetura

```text
Cliente HTTP
    |
    v
ItemController
    |
    v
ItemService
    |---> ValidadorItemFactory ---> ValidadorItem (Strategy)
    |---> ProcessadorItemFactory ---> ProcessadorItem (Strategy)
    |---> ItemObserver
    `---> ItemRepository ---> PostgreSQL
```

- **Strategy:** implementações de `ValidadorItem` validam e implementações de
  `ProcessadorItem` processam cada `TipoItem`.
- **Factory:** as Factories de validação e processamento selecionam as
  Strategies corretas.
- **Observer:** auditoria e notificação reagem ao processamento.
- **Validação:** cada `ValidadorItem` contém apenas a regra do tipo suportado.

Endpoints principais:

```text
POST   /itens
GET    /itens
GET    /itens?nome=abc
DELETE /itens/{id}
POST   /itens/{id}/processar
```

## Como executar

Requisitos: Java 25 e Docker Desktop.

```powershell
# Testes unitários
.\mvnw.cmd "-Dtest=ItemServiceTest,ValidadorItem*Test,ProcessadorItem*Test" test

# Todos os testes, cobertura e geração do JAR
.\mvnw.cmd clean verify

# Executar a API com um PostgreSQL disponível
.\mvnw.cmd spring-boot:run
```

Para executar fora do Compose, configure `DB_URL`, `DB_USERNAME` e
`DB_PASSWORD`. No Linux e no GitHub Actions, use `./mvnw` no lugar de
`.\mvnw.cmd`.

## Docker

Crie o arquivo local de configuração:

```powershell
Copy-Item .env.example .env
```

Defina no `.env` a imagem e senhas locais. `POSTGRES_PASSWORD` e `DB_PASSWORD`
devem ter o mesmo valor. O arquivo `.env` não é versionado.

Comandos úteis:

```powershell
# Gerar o JAR e construir a imagem
.\mvnw.cmd clean package -DskipTests
docker build -t seu_usuario/template-pi-api:latest .

# Iniciar ou atualizar os serviços
docker compose up -d

# Consultar estado e logs
docker compose ps
docker compose logs -f api
docker compose logs -f db

# Reiniciar somente a API
docker compose restart api

# Parar os serviços sem apagar o banco
docker compose down

# Parar e apagar também o volume do PostgreSQL
docker compose down -v
```

## Docker Hub

Crie no Docker Hub o repositório `template-pi-api` e execute:

```powershell
docker login
docker build -t seu_usuario/template-pi-api:latest .
docker push seu_usuario/template-pi-api:latest
```

No `.env`, use:

```dotenv
DOCKER_IMAGE=seu_usuario/template-pi-api
IMAGE_TAG=latest
```

## CI/CD

O workflow de testes executa `clean verify` nos Pull Requests. O workflow de
deploy executa em pushes para `main`, publica as tags `latest` e SHA no Docker
Hub e atualiza o Compose em `/opt/template-pi` na EC2.

Configure o ambiente `producao` no GitHub:

| Tipo | Nome |
|---|---|
| Variable | `DOCKERHUB_USERNAME` |
| Secret | `DOCKERHUB_TOKEN` |
| Secret | `AWS_HOST` |
| Secret | `AWS_USER` |
| Secret | `AWS_SSH_KEY` |
| Secret | `AWS_KNOWN_HOSTS` |

Na EC2, mantenha estes arquivos:

```text
/opt/template-pi/compose.yaml
/opt/template-pi/.env
```

O deploy usa somente a branch `main`. Não coloque senhas, tokens, chaves ou
endereços reais no repositório.
