# SEI-RS Municípios

Sistema simples de acompanhamento da implantação do SEI nos municípios do RS: cadastro de
municípios, roadmap de etapas, checklist de atividades por etapa e barras de progresso.

- **Backend**: Java 21 + Spring Boot 3, PostgreSQL, Flyway.
- **Frontend**: Angular 20 (standalone components) + Tailwind CSS v4.
- **Deploy**: Docker Compose (Postgres + backend + nginx servindo o frontend e fazendo proxy
  de `/api` pro backend).

Cada município novo já nasce com as 7 etapas do Plano de Trabalho de Kick-off (Apresentação,
Planejamento, Capacitação e Comunicação, Mapeamento de Fluxo e Gestão Documental, Definição do
Suporte, Go-live, Monitoramento e Avaliação) e os itens de checklist correspondentes — tudo
editável depois (adicionar/editar/remover etapas e itens).

## Rodando localmente (desenvolvimento)

Backend (precisa de um Postgres local, ex. via Docker):

```bash
docker run -d --name seimunicipios-db -e POSTGRES_DB=seimunicipios \
  -e POSTGRES_USER=seimunicipios -e POSTGRES_PASSWORD=seimunicipios \
  -p 5432:5432 postgres:16-alpine

cd backend
mvn spring-boot:run
```

Frontend (em outro terminal; o `npm start` já usa `proxy.conf.json` apontando pro backend em
`http://localhost:8080`):

```bash
cd frontend
npm install
npm start
```

Acesse `http://localhost:4200`.

## Deploy na VPS (Docker Compose)

1. Copie a pasta do projeto para a VPS (`scp -r`, `git clone`, etc.).
2. Crie o arquivo de variáveis de ambiente a partir do exemplo e ajuste a senha do banco:

   ```bash
   cp .env.example .env
   nano .env   # defina POSTGRES_PASSWORD e, se necessário, HTTP_PORT / CORS_ALLOWED_ORIGINS
   ```

3. Suba a stack:

   ```bash
   docker compose up -d --build
   ```

4. Confira se os containers subiram:

   ```bash
   docker compose ps
   docker compose logs -f backend
   ```

5. Libere a porta configurada em `HTTP_PORT` (padrão `80`) no firewall da VPS, se necessário
   (ex. `ufw allow 80/tcp`).

6. Acesse `http://<ip-ou-dominio-da-vps>` — o nginx serve o Angular e encaminha `/api/*` para o
   backend automaticamente.

### Verificação rápida

```bash
curl http://localhost/api/health          # deve responder {"status":"UP"}
curl http://localhost/api/municipios      # lista de municípios (vazia no início)
```

### Atualizando uma versão nova

```bash
git pull   # ou copie os arquivos atualizados
docker compose up -d --build
```

### Backup do banco

Os dados ficam no volume Docker `pgdata`. Para um dump manual:

```bash
docker compose exec db pg_dump -U seimunicipios seimunicipios > backup.sql
```

## Estrutura do projeto

```
sei-rs-municipios/
├── backend/            # Spring Boot (Java 21)
├── frontend/           # Angular 20 + Tailwind
├── docker-compose.yml
├── .env.example
└── README.md
```
