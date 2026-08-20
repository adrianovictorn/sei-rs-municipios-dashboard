-- A ordem de etapa/checklist_item passa a ser opcional: nula = segue a ordem do
-- template ao vivo; preenchida = o municipio reordenou essa fase/tarefa localmente e
-- essa preferencia passa a valer por cima do template (ver Etapa#getOrdemEfetiva /
-- ChecklistItem#getOrdemEfetiva). Isso permite reordenar etapas por municipio sem
-- perder a propagacao automatica de quem nunca mexeu na ordem.
--
-- Para linhas ja vinculadas a um template, a ordem local ate agora era sempre so um
-- espelho do valor do template (nunca havia UI pra divergir) -- zeramos ela pra essas
-- linhas voltarem a seguir o template ao vivo por padrao. Linhas customizadas (sem
-- template) mantêm sua ordem atual, que continua sendo a unica fonte pra elas.

ALTER TABLE etapa
    ALTER COLUMN ordem DROP NOT NULL,
    ALTER COLUMN ordem DROP DEFAULT;

UPDATE etapa SET ordem = NULL WHERE etapa_template_id IS NOT NULL;

ALTER TABLE checklist_item
    ALTER COLUMN ordem DROP NOT NULL,
    ALTER COLUMN ordem DROP DEFAULT;

UPDATE checklist_item SET ordem = NULL WHERE checklist_item_template_id IS NOT NULL;
