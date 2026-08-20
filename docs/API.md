# Referência de API

Base: `/api`. Sem autenticação. Corpo em JSON. Erros seguem o formato:

```json
{ "timestamp": "...", "status": 404, "error": "Not Found", "message": "Município não encontrado: 9" }
```

`400` (validação ou regra de negócio — ex. tentar renomear uma fase vinculada a
template), `404` (não encontrado). Ver `GlobalExceptionHandler`.

## Saúde

| Método | Rota | Descrição |
|---|---|---|
| GET | `/api/health` | `{"status":"UP"}` |

## Municípios

| Método | Rota | Corpo | Descrição |
|---|---|---|---|
| GET | `/api/municipios` | — | Lista resumida (progresso, equipe, `parado`, etc.) |
| GET | `/api/municipios/{id}` | — | Detalhe completo, com `etapas[]` e `checklistItems[]` aninhados |
| POST | `/api/municipios` | `MunicipioRequest` | Cria; já nasce com as fases/tarefas do template padrão vinculadas |
| PUT | `/api/municipios/{id}` | `MunicipioRequest` | Substitui os dados cadastrais (não mexe em etapas) |
| DELETE | `/api/municipios/{id}` | — | Apaga o município e cascata (etapas, itens, agendas) |

`MunicipioRequest`: `nome*`, `codigoIbge`, `regiao`, `populacao`,
`patrocinadorExecutivo`, `pontoFocalNome`, `pontoFocalEmail`, `pontoFocalTelefone`,
`dataInicio`, `dataPrevistaGolive`, `observacoes`, `equipeId`, `parado`.

## Etapas (fases de um município)

| Método | Rota | Corpo | Descrição |
|---|---|---|---|
| POST | `/api/municipios/{municipioId}/etapas` | `EtapaRequest` | Cria fase customizada (fora do template) |
| PUT | `/api/etapas/{id}` | `EtapaRequest` | Atualiza; **400** se tentar mudar nome/descrição/ordem de fase vinculada a template |
| DELETE | `/api/etapas/{id}` | — | Remove a fase (e seus itens) desse município |

`EtapaRequest`: `nome*`, `descricao`, `ordem`, `dataSolicitacao`, `dataInicio`,
`dataFim`, `duracaoDias`, `percentualPrevisto`, `predecessoras`.

## Itens de checklist (tarefas de uma etapa)

| Método | Rota | Corpo | Descrição |
|---|---|---|---|
| POST | `/api/etapas/{etapaId}/checklist-items` | `ChecklistItemRequest` | Cria item customizado |
| PUT | `/api/checklist-items/{id}` | `ChecklistItemUpdateRequest` | Atualização parcial (só aplica campos não nulos); **400** se tentar mudar descrição/ordem de item vinculado a template |
| DELETE | `/api/checklist-items/{id}` | — | Remove o item |

`ChecklistItemRequest` (criação): `descricao*`, `ordem`, `dataInicio`, `dataFim`,
`duracaoDias`, `percentualPrevisto`, `predecessoras`.
`ChecklistItemUpdateRequest` (atualização parcial): `descricao`, `concluido`, `ordem`,
`dataInicio`, `dataFim`, `duracaoDias`, `percentualPrevisto`, `predecessoras`.

## Fases padrão (template global)

| Método | Rota | Corpo | Descrição |
|---|---|---|---|
| GET | `/api/etapa-templates` | — | Lista todas as fases padrão com suas tarefas |
| POST | `/api/etapa-templates` | `EtapaTemplateRequest` | Cria fase padrão nova; propaga pra todos os municípios existentes |
| PUT | `/api/etapa-templates/{id}` | `EtapaTemplateRequest` | Atualiza; reflete ao vivo em todo município vinculado |
| DELETE | `/api/etapa-templates/{id}?emTodosMunicipios=false` | — | `false`/omitido: desvincula preservando texto local. `true`: apaga a fase **e o progresso** de todos os municípios vinculados (irreversível) |
| POST | `/api/etapa-templates/{faseId}/itens` | `ChecklistItemTemplateRequest` | Cria tarefa padrão nova; propaga pra todo município vinculado à fase |
| PUT | `/api/checklist-item-templates/{id}` | `ChecklistItemTemplateRequest` | Atualiza; reflete ao vivo |
| DELETE | `/api/checklist-item-templates/{id}?emTodosMunicipios=false` | — | Mesma semântica do delete de fase, no nível de tarefa |

`EtapaTemplateRequest`: `nome*`, `descricao`, `ordem`, `duracaoDias`, `exibirMatriz`,
`ordemMatriz`. `ChecklistItemTemplateRequest`: `descricao*`, `ordem`, `duracaoDias`.

## Equipes

| Método | Rota | Corpo | Descrição |
|---|---|---|---|
| GET | `/api/equipes` | — | Lista |
| POST | `/api/equipes` | `EquipeRequest` (`nome*`) | Cria |
| PUT | `/api/equipes/{id}` | `EquipeRequest` | Renomeia |
| DELETE | `/api/equipes/{id}` | — | Remove; municípios associados ficam sem equipe |

## Tipos de agenda

| Método | Rota | Corpo | Descrição |
|---|---|---|---|
| GET | `/api/tipos-agenda` | — | Lista |
| POST | `/api/tipos-agenda` | `TipoAgendaRequest` (`nome*`) | Cria |
| PUT | `/api/tipos-agenda/{id}` | `TipoAgendaRequest` | Renomeia |
| DELETE | `/api/tipos-agenda/{id}` | — | Remove; agendas associadas ficam sem tipo |

## Agendas

| Método | Rota | Corpo | Descrição |
|---|---|---|---|
| GET | `/api/agendas` | — | Lista todas as agendas de todos os municípios (`municipioNome`/`tipoAgendaNome` já resolvidos) |
| POST | `/api/municipios/{municipioId}/agendas` | `AgendaRequest` | Cria agenda pra esse município |
| PUT | `/api/agendas/{id}` | `AgendaRequest` | Atualiza |
| DELETE | `/api/agendas/{id}` | — | Remove |

`AgendaRequest`: `titulo*`, `dataHora*` (ISO `yyyy-MM-ddTHH:mm`), `tipoAgendaId`,
`local`, `observacoes`, `realizada`.
