CREATE TABLE equipe (
    id    BIGSERIAL PRIMARY KEY,
    nome  VARCHAR(150) NOT NULL
);

ALTER TABLE municipio
    ADD COLUMN equipe_id  BIGINT REFERENCES equipe(id) ON DELETE SET NULL,
    ADD COLUMN parado     BOOLEAN NOT NULL DEFAULT FALSE;

CREATE INDEX idx_municipio_equipe ON municipio(equipe_id);

ALTER TABLE etapa_template
    ADD COLUMN exibir_matriz  BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN ordem_matriz   INTEGER;

ALTER TABLE etapa
    ADD COLUMN data_solicitacao DATE;

-- "Capacitacao e Comunicacao" e "Mapeamento de Fluxo e Gestao Documental" viram as
-- colunas "Capacitacao" e "Mapeamento" da matriz. Mantem o mesmo id/descricao/itens
-- (zero perda de progresso ja registrado pelos municipios vinculados ao template).
UPDATE etapa_template
    SET nome = 'Capacitação', exibir_matriz = TRUE, ordem_matriz = 5
    WHERE nome = 'Capacitação e Comunicação';

UPDATE etapa_template
    SET nome = 'Mapeamento', exibir_matriz = TRUE, ordem_matriz = 1
    WHERE nome = 'Mapeamento de Fluxo e Gestão Documental';

-- As 4 fases que faltam para completar as 6 colunas da matriz (Ambiente, Normatizacao,
-- Parametrizacao, Comunicacao) sao novas. Cada uma e criada com 2 tarefas padrao e
-- propagada para todo municipio ja existente -- o mesmo efeito que
-- EtapaTemplateService.createFase()/addTarefa() fazem em runtime quando um admin cria
-- uma fase padrao nova pela tela, só que aqui direto em SQL porque e uma migracao.
DO $$
DECLARE
    ambiente_id BIGINT;
    normatizacao_id BIGINT;
    parametrizacao_id BIGINT;
    comunicacao_id BIGINT;
BEGIN
    INSERT INTO etapa_template (nome, descricao, ordem, exibir_matriz, ordem_matriz)
        VALUES ('Ambiente', 'Configuração do ambiente técnico de acesso ao sistema.', 7, TRUE, 0)
        RETURNING id INTO ambiente_id;
    INSERT INTO checklist_item_template (etapa_template_id, descricao, ordem) VALUES
        (ambiente_id, 'Configuração do ambiente de acesso (URLs, usuários, permissões)', 0),
        (ambiente_id, 'Validação de infraestrutura local (rede, equipamentos)', 1);

    INSERT INTO etapa_template (nome, descricao, ordem, exibir_matriz, ordem_matriz)
        VALUES ('Normatização', 'Levantamento e publicação de normas e regras documentais.', 8, TRUE, 2)
        RETURNING id INTO normatizacao_id;
    INSERT INTO checklist_item_template (etapa_template_id, descricao, ordem) VALUES
        (normatizacao_id, 'Levantamento de normas e decretos aplicáveis', 0),
        (normatizacao_id, 'Publicação/atualização de portarias e regulamentos internos', 1);

    INSERT INTO etapa_template (nome, descricao, ordem, exibir_matriz, ordem_matriz)
        VALUES ('Parametrização', 'Configuração inicial do sistema para o município.', 9, TRUE, 3)
        RETURNING id INTO parametrizacao_id;
    INSERT INTO checklist_item_template (etapa_template_id, descricao, ordem) VALUES
        (parametrizacao_id, 'Parametrização inicial (perfis, unidades, tipos de processo)', 0),
        (parametrizacao_id, 'Testes de parametrização e homologação', 1);

    INSERT INTO etapa_template (nome, descricao, ordem, exibir_matriz, ordem_matriz)
        VALUES ('Comunicação', 'Plano e canais de comunicação da implantação.', 10, TRUE, 4)
        RETURNING id INTO comunicacao_id;
    INSERT INTO checklist_item_template (etapa_template_id, descricao, ordem) VALUES
        (comunicacao_id, 'Plano de comunicação interna sobre a implantação', 0),
        (comunicacao_id, 'Divulgação de canais e materiais oficiais', 1);

    -- Propaga cada fase nova para todo municipio ja existente.
    INSERT INTO etapa (municipio_id, etapa_template_id, nome, descricao, ordem)
        SELECT m.id, ambiente_id, 'Ambiente', 'Configuração do ambiente técnico de acesso ao sistema.', 7
        FROM municipio m;
    INSERT INTO checklist_item (etapa_id, checklist_item_template_id, descricao, ordem)
        SELECT e.id, cit.id, cit.descricao, cit.ordem
        FROM etapa e
        JOIN checklist_item_template cit ON cit.etapa_template_id = ambiente_id
        WHERE e.etapa_template_id = ambiente_id;

    INSERT INTO etapa (municipio_id, etapa_template_id, nome, descricao, ordem)
        SELECT m.id, normatizacao_id, 'Normatização', 'Levantamento e publicação de normas e regras documentais.', 8
        FROM municipio m;
    INSERT INTO checklist_item (etapa_id, checklist_item_template_id, descricao, ordem)
        SELECT e.id, cit.id, cit.descricao, cit.ordem
        FROM etapa e
        JOIN checklist_item_template cit ON cit.etapa_template_id = normatizacao_id
        WHERE e.etapa_template_id = normatizacao_id;

    INSERT INTO etapa (municipio_id, etapa_template_id, nome, descricao, ordem)
        SELECT m.id, parametrizacao_id, 'Parametrização', 'Configuração inicial do sistema para o município.', 9
        FROM municipio m;
    INSERT INTO checklist_item (etapa_id, checklist_item_template_id, descricao, ordem)
        SELECT e.id, cit.id, cit.descricao, cit.ordem
        FROM etapa e
        JOIN checklist_item_template cit ON cit.etapa_template_id = parametrizacao_id
        WHERE e.etapa_template_id = parametrizacao_id;

    INSERT INTO etapa (municipio_id, etapa_template_id, nome, descricao, ordem)
        SELECT m.id, comunicacao_id, 'Comunicação', 'Plano e canais de comunicação da implantação.', 10
        FROM municipio m;
    INSERT INTO checklist_item (etapa_id, checklist_item_template_id, descricao, ordem)
        SELECT e.id, cit.id, cit.descricao, cit.ordem
        FROM etapa e
        JOIN checklist_item_template cit ON cit.etapa_template_id = comunicacao_id
        WHERE e.etapa_template_id = comunicacao_id;
END $$;
