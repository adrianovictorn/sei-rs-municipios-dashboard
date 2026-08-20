# Arquitetura e modelo de dados

Este documento explica como o sistema é modelado por dentro e por quê — para quem vai
mexer no código depois. Para instruções de uso/deploy, veja o [README](../README.md);
para a lista de endpoints, veja [API.md](./API.md).

## Visão geral

```
Município ──< Etapa ──< ChecklistItem
    │             │
    │             └──> EtapaTemplate ──< ChecklistItemTemplate
    │
    ├──> Equipe
    └──< Agenda ──> TipoAgenda
```

- Um **Município** tem várias **Etapas** (fases do processo de implantação), cada uma
  com vários **ChecklistItem** (tarefas).
- As fases/tarefas **padrão** (usadas por todo município) vivem em `EtapaTemplate`/
  `ChecklistItemTemplate` — uma entidade separada, não hardcoded em código.
- Município pode ter uma **Equipe** responsável e vários **Agenda** (reuniões/
  compromissos), cada Agenda com um **TipoAgenda**.

## O padrão "template com vínculo ao vivo"

Esse é o design mais importante do sistema, usado duas vezes (fases padrão e — se
crescer no futuro — poderia se repetir para outras listas de referência). Vale entender
bem antes de mexer em `etapa`, `checklist_item`, `etapa_template` ou
`checklist_item_template`.

**Problema que resolve:** antes desse design, as 7 fases padrão viviam hardcoded numa
classe Java (`EtapaSeed`) e eram *copiadas* pra cada município no momento da criação.
Editar uma fase depois exigia mudar código e fazer deploy, e cada município já tinha uma
cópia própria, desconectada — não tinha como propagar um ajuste de nome ou tarefa pra
quem já existia.

**Como funciona agora:**

- `EtapaTemplate`/`ChecklistItemTemplate` são as fases/tarefas padrão, guardadas no
  banco, editáveis pela tela **Fases padrão** (`/fases-padrao`), sem precisar de deploy.
- Toda `Etapa`/`ChecklistItem` de um município tem uma coluna opcional
  `etapa_template_id`/`checklist_item_template_id`. Quando esse vínculo existe, os
  campos `nome`/`descricao`/`ordem` da linha do município **não são a fonte visível** —
  a exibição lê esses valores **ao vivo** do template, através dos getters "efetivos"
  (`Etapa.getNomeEfetivo()`, `getDescricaoEfetiva()`, `getOrdemEfetiva()`, e o
  equivalente em `ChecklistItem`). Editar o template em
  `EtapaTemplateService.updateFase()`/`updateTarefa()` não precisa propagar nada — o
  próximo `GET` de qualquer município já reflete a mudança.
- As colunas `nome`/`descricao`/`ordem` da linha do município continuam existindo e são
  atualizadas como **snapshot de segurança**: se o template for apagado (só o botão
  "remover", não "remover de todos"), a linha do município é desvinculada
  (`template_id = null`) e volta a ser uma fase/tarefa customizada, com o último texto
  conhecido — nunca perde o dado.
- **Propagação em criação**: criar uma fase/tarefa padrão nova
  (`EtapaTemplateService.createFase()`/`addTarefa()`) cria automaticamente a linha
  correspondente, já vinculada, em **todos os municípios existentes** — e todo
  município criado depois nasce com o vínculo (`MunicipioService.seedEtapas()`, que lê
  o template em vez de qualquer coisa hardcoded).
- **Bloqueio de edição direta**: `EtapaService.updateEtapa()`/
  `ChecklistItemService.updateItem()` recusam (400) uma tentativa de mudar nome/
  descrição/ordem de uma linha vinculada a template — precisa editar pelo template
  global. Campos de cronograma (datas, duração, % previsto, predecessoras,
  `concluido`) continuam sempre editáveis por município, vinculado ou não.
- **Município que já existia antes da migração `V2`** (que criou esse sistema) **não é
  retroativamente vinculado** — suas fases continuam soltas, editáveis normalmente,
  simplesmente fora do vínculo. Foi uma decisão deliberada pra não ter que adivinhar
  qual linha antiga corresponde a qual template.

### Remover de verdade ("remover de todos")

Além do "remover" normal (desvincula e preserva, descrito acima),
`EtapaTemplateService.deleteFaseEmTodosMunicipios()`/`deleteTarefaEmTodosMunicipios()`
apagam a fase/tarefa **e o progresso já registrado** em todos os municípios vinculados —
irreversível. Exposto via `DELETE .../{id}?emTodosMunicipios=true`, com um botão
separado (vermelho, confirmação reforçada) na tela Fases padrão. Use só quando quiser
mesmo sumir com a fase em todo lugar, não só ajustar o padrão.

## Matriz de status (`/matriz-status`)

Tela de leitura rápida (município × fase), inspirada numa planilha de acompanhamento
que a coordenação já usava fora do sistema. Não é uma entidade própria — é montada no
frontend a partir de dados que já existem:

- `EtapaTemplate` ganhou `exibirMatriz` (boolean) e `ordemMatriz` (inteiro): só as
  fases marcadas aparecem como coluna, na ordem definida (editável em Fases padrão,
  sem precisar mexer em código pra mudar quais fases aparecem na matriz). Hoje são 6:
  Ambiente, Mapeamento, Normatização, Parametrização, Comunicação, Capacitação — nomes
  que vieram de dividir as fases "Capacitação e Comunicação" e "Mapeamento de Fluxo e
  Gestão Documental" em duas cada, mais duas fases novas (migração `V3`).
- Cada célula (`calcularCelula()` em `matriz-status.model.ts`) deriva o status da
  `Etapa` correspondente daquele município (achada pelo `templateId`):
  - **SIM** (verde): progresso 100%.
  - **NÃO** (vermelho): progresso 0%, sem `dataSolicitacao`.
  - **PARCIAL X%** (amarelo): 0% < progresso < 100%.
  - **Solicitado DD/MM** (azul): progresso 0% mas com `dataSolicitacao` preenchida.
  - Selo auxiliar de **previsão** (calendário azul, ou vermelho/⚠ se `dataFim` já
    passou e progresso < 100%) aparece ao lado do badge principal quando a etapa tem
    `dataFim` e ainda não está concluída.
- Coluna **Go-live previsto**: não é uma fase, é o campo `Município.dataPrevistaGolive`
  — como a fase "Go-live" não é uma das 6 colunas da matriz, esse campo garante um
  lugar de leitura rápida pra essa data específica.
- Coluna **Equipe**: mostra `Município.equipe.nome`, ou "parado" em vermelho quando
  `Município.parado = true` (independe de qual equipe está associada).
- Auto-refresh por polling (30s) + botão manual — não há WebSocket/SSE.
- Exportação Excel via `exceljs` (cores dos badges preservadas nas células) e PDF via
  impressão do navegador (`window.print()` + CSS `@media print` dedicado em
  `styles.css`, que esconde a navegação e força `print-color-adjust: exact`).

## Agendas (`/agendas`)

Reuniões/compromissos por município (`Agenda`), categorizados por `TipoAgenda`. Os
tipos (ex.: Comunicação, SEI Usar, SEI Administrar) são só **dado inicial** inserido
pela migração `V4` — nada hardcoded em código — e totalmente editáveis pela própria
tela (seção "Gerenciar tipos"), o mesmo espírito do `EtapaTemplate`. Página global
(cross-município), não uma aba dentro do município: carrega tudo de uma vez e filtra
no cliente, mesmo padrão já usado em `atividades-kanban.ts`.

## Entidades — referência rápida

| Entidade | Tabela | Chave estrangeira | Observação |
|---|---|---|---|
| `Municipio` | `municipio` | `equipe_id` → `equipe` (nullable) | `parado`, `dataPrevistaGolive` |
| `Etapa` | `etapa` | `municipio_id`; `etapa_template_id` → `etapa_template` (nullable) | getters efetivos; `dataSolicitacao`, `dataInicio`, `dataFim`, `duracaoDias`, `percentualPrevisto`, `predecessoras` |
| `ChecklistItem` | `checklist_item` | `etapa_id`; `checklist_item_template_id` → `checklist_item_template` (nullable) | `concluido`, `dataConclusao`, mesmos campos de cronograma que `Etapa` |
| `EtapaTemplate` | `etapa_template` | — | `exibirMatriz`, `ordemMatriz` |
| `ChecklistItemTemplate` | `checklist_item_template` | `etapa_template_id` | |
| `Equipe` | `equipe` | — | só `nome` |
| `TipoAgenda` | `tipo_agenda` | — | só `nome` |
| `Agenda` | `agenda` | `municipio_id`; `tipo_agenda_id` → `tipo_agenda` (nullable) | `titulo`, `dataHora`, `local`, `observacoes`, `realizada` |

`Etapa.getProgresso()`/`getStatus()` (`NAO_INICIADA`/`EM_ANDAMENTO`/`CONCLUIDA`) são
sempre calculados a partir dos `ChecklistItem.concluido`, nunca guardados — mesma coisa
pro progresso geral de um município (`MunicipioDetailResponse`/`SummaryResponse`), que é
a média do progresso das etapas.

## Migrações Flyway

| Arquivo | O que faz |
|---|---|
| `V1__init.sql` | `municipio`, `etapa`, `checklist_item` — schema original. |
| `V2__etapa_template.sql` | Cria `etapa_template`/`checklist_item_template`, semeia as 7 fases padrão originais, adiciona campos de cronograma e o vínculo `*_template_id` em `etapa`/`checklist_item`. |
| `V3__matriz_status.sql` | Cria `equipe`; `municipio.equipe_id`/`parado`; `etapa_template.exibir_matriz`/`ordem_matriz`; `etapa.data_solicitacao`; renomeia/divide fases pra formar as 6 colunas da matriz e propaga as novas pra municípios já existentes. |
| `V4__agendas_golive.sql` | `municipio.data_prevista_golive`; cria `tipo_agenda`/`agenda`; semeia 3 tipos iniciais. |

Nunca edite uma migração já aplicada em qualquer ambiente (mesmo local) — crie uma nova
(`V5__...sql`). O Flyway valida checksum e recusa migrações alteradas depois de
aplicadas.
