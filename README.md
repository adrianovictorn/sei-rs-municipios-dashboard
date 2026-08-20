# SEI-RS Municípios

Sistema de acompanhamento da implantação do SEI (Sistema Eletrônico de Informações) nos
municípios do Rio Grande do Sul: cadastro de municípios, roadmap de fases, checklist de
tarefas por fase, matriz de status de leitura rápida, agenda de reuniões e cadastro de
equipes responsáveis.

- **Backend**: Java 21 + Spring Boot 3, PostgreSQL, Flyway.
- **Frontend**: Angular 20 (standalone components, signals) + Tailwind CSS v4.
- **Deploy**: Docker Compose (Postgres + backend + nginx servindo o frontend e fazendo
  proxy de `/api` pro backend).

Documentação complementar:

- [`docs/ARQUITETURA.md`](docs/ARQUITETURA.md) — modelo de dados, decisões de design
  (por que fases padrão funcionam como "template com vínculo ao vivo", como a matriz de
  status é montada, migrações).
- [`docs/API.md`](docs/API.md) — referência de todos os endpoints REST.

## Funcionalidades

- **Municípios**: cadastro (dados de contato, ponto focal, equipe responsável, data de
  início, data prevista de Go-live), listagem com progresso geral, tela de detalhe.
- **Fases e tarefas por município**: cada município nasce com as fases/tarefas do
  **template padrão** (ver abaixo), com campos de cronograma (datas de solicitação,
  início, término, duração, % previsto, predecessoras), e pode ganhar fases/tarefas
  próprias além do padrão. Progresso e status (não iniciada/em andamento/concluída) são
  calculados a partir da conclusão das tarefas.
- **Fases padrão** (`/fases-padrao`): tela de administração do template global usado
  por todo município novo. Editar nome/descrição de uma fase padrão aqui reflete
  automaticamente em todos os municípios que ainda estiverem vinculados a ela — sem
  precisar editar um por um nem fazer deploy. Também dá pra apagar de vez (fase e
  progresso) de todos os municípios vinculados, quando é isso mesmo que se quer.
- **Matriz de status** (`/matriz-status`): leitura rápida município × fase (6 colunas:
  Ambiente, Mapeamento, Normatização, Parametrização, Comunicação, Capacitação), com
  badges SIM/NÃO/PARCIAL/Solicitado, alerta visual de atraso, filtros e ordenação,
  observação editável por município, auto-atualização, e exportação em Excel/PDF.
- **Agendas** (`/agendas`): reuniões e compromissos por município, categorizados por
  tipo (Comunicação, SEI Usar, SEI Administrar, ou outros tipos cadastrados na hora —
  nada fixo em código).
- **Equipes** (`/equipes`): cadastro simples das equipes responsáveis pelos municípios.
- **Atividades** (`/atividades`): quadro cruzando as tarefas pendentes/concluídas de
  todos os municípios.

## Estrutura do projeto

```
sei-rs-municipios/
├── backend/            # Spring Boot (Java 21) — um pacote por domínio
│   └── src/main/java/br/gov/rs/seimunicipios/
│       ├── municipio/       agenda/        equipe/
│       ├── etapa/           checklist/     tipoagenda/
│       └── common/          (exceções e handler globais)
├── frontend/           # Angular 20 + Tailwind
│   └── src/app/
│       ├── core/             modelos e serviços HTTP (um par por entidade)
│       ├── features/         uma pasta por tela (municipios, fases-padrao,
│       │                     matriz-status, agendas, equipes, atividades)
│       └── shared/components progress-bar, roadmap, status-badge
├── docs/                ARQUITETURA.md, API.md
├── docker-compose.yml
├── .env.example
└── README.md
```

## Rodando localmente (desenvolvimento)

Backend (precisa de um Postgres local, ex. via Docker):

```bash
docker run -d --name seimunicipios-db -e POSTGRES_DB=seimunicipios \
  -e POSTGRES_USER=seimunicipios -e POSTGRES_PASSWORD=seimunicipios \
  -p 5432:5432 postgres:16-alpine

cd backend
mvn spring-boot:run
```

O Flyway aplica as migrações automaticamente na subida (`backend/src/main/resources/db/
migration`) — nenhum passo manual de schema é necessário.

Frontend (em outro terminal; o `npm start` já usa `proxy.conf.json` apontando pro
backend em `http://localhost:8080`):

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

5. Libere a porta configurada em `HTTP_PORT` (padrão `80`) no firewall da VPS, se
   necessário (ex. `ufw allow 80/tcp`).

6. Acesse `http://<ip-ou-dominio-da-vps>` — o nginx serve o Angular e encaminha
   `/api/*` para o backend automaticamente.

### Verificação rápida

```bash
curl http://localhost/api/health          # deve responder {"status":"UP"}
curl http://localhost/api/municipios      # lista de municípios
curl http://localhost/api/etapa-templates # fases padrão configuradas
```

### Atualizando uma versão nova

```bash
git pull   # ou copie os arquivos atualizados
docker compose up -d --build
```

As migrações Flyway novas (`V5__...sql` em diante) rodam sozinhas na subida do backend,
por cima do schema existente — sem perder dado já cadastrado.

### Backup do banco

Os dados ficam no volume Docker `pgdata`. Antes de aplicar uma atualização com migração
de banco nova, vale a pena fazer um dump manual:

```bash
docker compose exec db pg_dump -U seimunicipios seimunicipios > backup.sql
```

Para restaurar: `docker compose exec -T db psql -U seimunicipios seimunicipios < backup.sql`.
