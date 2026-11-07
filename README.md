# EcoRota API

API REST central do **EcoRota**, que conecta a agricultura familiar à merenda escolar (PNAE).
Produtores cadastram colheitas, escolas públicas compram direto pela cota obrigatória de 45% da
agricultura familiar (Lei 11.947/2009, alterada pela Lei 15.226/2025), e a API controla o estoque
e o valor de cada pedido.

Global Solution 2026 · Microservice and Web Engineering & IT Services · Turma 3SIR

| Item | Link |
|---|---|
| API no Azure | `https://<SEU-APP>.azurewebsites.net` *(preencher)* |
| Swagger | `https://<SEU-APP>.azurewebsites.net/` (redireciona para `/swagger-ui.html`) |
| Docker Hub | `https://hub.docker.com/r/<SEU-USUARIO>/ecorota-api` *(preencher)* |
| Workflow | [.github/workflows/docker-publish.yml](.github/workflows/docker-publish.yml) |

## Stack

Java 21 · Spring Boot 3.3 · Spring Data JPA · Microsoft SQL Server · ModelMapper · Bean Validation ·
springdoc-openapi (Swagger) · Docker · GitHub Actions · Azure

## Arquitetura

```
controller  ->  service  ->  repository  ->  SQL Server
   |  (DTO)        |  (regras de negócio)
   dto  <-- ModelMapper -->  entity
exception (GlobalExceptionHandler: erros padronizados, sem stack trace)
```

Os controllers só falam com os services; o acesso ao banco fica nos repositories.

### Entidades (tabelas)

| Entidade | Tabela | Atributos | Relacionamento |
|---|---|---|---|
| Produtor | `produtores` | id, nome*, cpf* (único), numeroCaf, telefone*, municipio*, dataCadastro* | 1:N colheitas |
| Colheita | `colheitas` | id, produto*, quantidade*, unidade*, precoUnitario*, dataColheita*, janelaVendaDias | N:1 produtor, 1:N pedidos |
| Escola | `escolas` | id, nome*, codigoInep* (único), municipio*, numeroAlunos, orcamentoPnae*, email | 1:N pedidos |
| Pedido | `pedidos` | id, quantidade*, valorTotal*, status*, dataEntrega* | N:1 escola, N:1 colheita |

\* obrigatório

### Regras de negócio (PedidoService)

- O pedido não pode passar da quantidade disponível na colheita (retorna 400).
- Criar um pedido reserva o estoque da colheita; excluir o pedido ou mudar o status para `CANCELADO` devolve o estoque.
- `valorTotal = quantidade × precoUnitario` é calculado pela API, não pelo cliente.

## Endpoints (v1)

Cada recurso (`produtores`, `colheitas`, `escolas`, `pedidos`) expõe:

| Método | Rota | Sucesso |
|---|---|---|
| POST | `/api/v1/{recurso}` | 201 Created + header `Location` |
| GET | `/api/v1/{recurso}` | 200 OK |
| GET | `/api/v1/{recurso}/{id}` | 200 OK |
| PUT | `/api/v1/{recurso}/{id}` | 200 OK |
| DELETE | `/api/v1/{recurso}/{id}` | 204 No Content |

Erros: **400** dados inválidos, JSON malformado ou estoque insuficiente · **404** recurso inexistente ·
**409** CPF/INEP duplicado · **500** mensagem genérica, sem detalhes internos. Formato:

```json
{ "timestamp": "...", "status": 404, "erro": "Not Found",
  "mensagem": "Pedido com id 99 nao encontrado(a)", "caminho": "/api/v1/pedidos/99", "detalhes": [] }
```

## Executar localmente

### Opção 1 — Docker Compose (SQL Server + API)

```bash
docker compose up --build
```

O compose sobe o SQL Server 2022, cria o banco `ecorota` e inicia a API em http://localhost:8080.
A senha local padrão pode ser trocada com a variável `DB_PASSWORD`.

### Opção 2 — Maven com um SQL Server existente

Crie o banco (`CREATE DATABASE ecorota;`), depois:

```bash
export DB_URL="jdbc:sqlserver://localhost:1433;databaseName=ecorota;encrypt=true;trustServerCertificate=true"
export DB_USER=sa
export DB_PASSWORD=<sua-senha>
mvn spring-boot:run
```

As tabelas são criadas automaticamente pelo Hibernate (`ddl-auto=update`).

### Testes automatizados

```bash
mvn verify
```

Os testes rodam com H2 em memória e cobrem o fluxo completo (produtor → colheita → escola → pedido), a
reserva e devolução de estoque e os erros 400/404/409.

## Configuração

| Variável | Descrição |
|---|---|
| `DB_URL` | JDBC URL do SQL Server |
| `DB_USER` | Usuário do banco |
| `DB_PASSWORD` | Senha do banco (**nunca** commitar) |
| `PORT` | Porta HTTP (padrão 8080) |

## Docker Hub

```bash
docker pull <SEU-USUARIO>/ecorota-api:latest
docker run -p 8080:8080 -e DB_URL=... -e DB_USER=... -e DB_PASSWORD=... <SEU-USUARIO>/ecorota-api:latest
```

Tags publicadas: `latest` e o número da execução do workflow (`1`, `2`, ...).

## Continuous Delivery (GitHub Actions)

A cada `push` na branch `main`, o workflow compila e testa (`mvn verify`), gera a imagem Docker e
publica no Docker Hub. Para configurar, em *Settings → Secrets and variables → Actions*, crie:

- `DOCKERHUB_USERNAME` — seu usuário do Docker Hub
- `DOCKERHUB_TOKEN` — Access Token gerado em *Docker Hub → Account Settings → Personal access tokens*

## Deploy no Azure

Exemplo com Azure CLI (Azure SQL + App Service for Containers):

```bash
RG=rg-ecorota; LOC=brazilsouth; SQL=ecorota-sql-<sufixo>; APP=ecorota-api-<sufixo>

az group create -n $RG -l $LOC

# SQL Server + banco
az sql server create -g $RG -n $SQL -l $LOC -u ecorotaadmin -p '<SENHA-FORTE>'
az sql db create -g $RG -s $SQL -n ecorota --service-objective Basic
az sql server firewall-rule create -g $RG -s $SQL -n azure --start-ip-address 0.0.0.0 --end-ip-address 0.0.0.0

# App Service (Linux) rodando a imagem do Docker Hub
az appservice plan create -g $RG -n plan-ecorota --is-linux --sku B1
az webapp create -g $RG -p plan-ecorota -n $APP -i <SEU-USUARIO>/ecorota-api:latest
az webapp config appsettings set -g $RG -n $APP --settings \
  WEBSITES_PORT=8080 \
  DB_URL="jdbc:sqlserver://$SQL.database.windows.net:1433;databaseName=ecorota;encrypt=true;trustServerCertificate=false" \
  DB_USER=ecorotaadmin DB_PASSWORD='<SENHA-FORTE>'
```

A API fica em `https://$APP.azurewebsites.net`. As credenciais ficam apenas nas *App Settings* do
Azure, nunca no repositório.

## Como testar

Abra o Swagger em `/` ou use curl (troque `BASE` pela URL do Azure):

```bash
BASE=http://localhost:8080/api/v1

curl -X POST $BASE/produtores -H "Content-Type: application/json" \
  -d '{"nome":"Ana Souza","cpf":"12345678901","numeroCaf":"CAF123","telefone":"11999990000","municipio":"Mogi das Cruzes"}'

curl -X POST $BASE/colheitas -H "Content-Type: application/json" \
  -d '{"produto":"Alface","quantidade":15,"unidade":"maco","precoUnitario":3.20,"dataColheita":"2026-11-07","janelaVendaDias":3,"produtorId":1}'

curl -X POST $BASE/escolas -H "Content-Type: application/json" \
  -d '{"nome":"EMEF Parelheiros","codigoInep":"35123456","municipio":"Sao Paulo","numeroAlunos":600,"orcamentoPnae":80000}'

curl -X POST $BASE/pedidos -H "Content-Type: application/json" \
  -d '{"escolaId":1,"colheitaId":1,"quantidade":10,"dataEntrega":"2026-11-20"}'

curl $BASE/colheitas/1      # quantidade caiu para 5 (estoque reservado)
curl $BASE/pedidos/99       # 404 padronizado
curl -X DELETE $BASE/pedidos/1   # 204; o estoque volta para 15
```

## Integrantes

| Nome | RM |
|---|---|
| Giovanni Conde | RM5651213 |
| Lucas Lima Franco | RM550255 |
| Bruno César Toledo | RM554878 |
