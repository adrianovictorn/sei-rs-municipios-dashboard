CREATE TABLE etapa_template (
    id            BIGSERIAL PRIMARY KEY,
    nome          VARCHAR(150) NOT NULL,
    descricao     VARCHAR(500),
    ordem         INTEGER NOT NULL DEFAULT 0,
    duracao_dias  INTEGER
);

CREATE TABLE checklist_item_template (
    id                  BIGSERIAL PRIMARY KEY,
    etapa_template_id  BIGINT NOT NULL REFERENCES etapa_template(id) ON DELETE CASCADE,
    descricao           VARCHAR(500) NOT NULL,
    ordem                INTEGER NOT NULL DEFAULT 0,
    duracao_dias        INTEGER
);

CREATE INDEX idx_checklist_item_template_etapa ON checklist_item_template(etapa_template_id);

ALTER TABLE etapa
    ADD COLUMN etapa_template_id     BIGINT REFERENCES etapa_template(id) ON DELETE SET NULL,
    ADD COLUMN data_inicio           DATE,
    ADD COLUMN data_fim              DATE,
    ADD COLUMN duracao_dias          INTEGER,
    ADD COLUMN percentual_previsto   INTEGER,
    ADD COLUMN predecessoras         VARCHAR(100);

CREATE INDEX idx_etapa_template ON etapa(etapa_template_id);

ALTER TABLE checklist_item
    ADD COLUMN checklist_item_template_id  BIGINT REFERENCES checklist_item_template(id) ON DELETE SET NULL,
    ADD COLUMN data_inicio                 DATE,
    ADD COLUMN data_fim                    DATE,
    ADD COLUMN duracao_dias                INTEGER,
    ADD COLUMN percentual_previsto         INTEGER,
    ADD COLUMN predecessoras               VARCHAR(100);

CREATE INDEX idx_checklist_item_template ON checklist_item(checklist_item_template_id);

-- Fases e tarefas padrao (mesmo conteudo antes hardcoded em EtapaSeed.java),
-- agora como dado editavel, unico por instalacao, referenciado pelos municipios.
INSERT INTO etapa_template (id, nome, descricao, ordem) VALUES
    (1, 'Apresentação', 'Kickoff e diagnóstico inicial do município.', 0),
    (2, 'Planejamento', 'Estrutura organizacional e preparação para a implantação.', 1),
    (3, 'Capacitação e Comunicação', 'Treinamentos e materiais de apoio.', 2),
    (4, 'Mapeamento de Fluxo e Gestão Documental', 'Processos, gargalos e regras documentais.', 3),
    (5, 'Definição do Suporte', 'Escopo e canal de suporte interno.', 4),
    (6, 'Go-live', 'Disponibilização assistida do sistema.', 5),
    (7, 'Monitoramento e Avaliação', 'Métricas, resultados e melhorias contínuas.', 6);

SELECT setval('etapa_template_id_seq', (SELECT MAX(id) FROM etapa_template));

INSERT INTO checklist_item_template (etapa_template_id, descricao, ordem) VALUES
    (1, 'Kickoff e definição da Equipe de Trabalho', 0),
    (1, 'Diagnóstico técnico e negocial', 1),
    (1, 'Cronograma macro', 2),
    (2, 'Estrutura organizacional (organograma e equipe)', 0),
    (2, 'Validação de acessos (planilhas e usuários)', 1),
    (2, 'Preparação para implantação', 2),
    (3, 'Capacitação específica (público alvo)', 0),
    (3, 'Treinamentos online', 1),
    (3, 'Disponibilização de cartilhas e vídeos práticos', 2),
    (4, 'Mapeamento de fluxos (processos, gargalos, melhorias)', 0),
    (4, 'Gestão documental (padronização, regras, normas legais)', 1),
    (5, 'Apoio na definição do suporte interno', 0),
    (5, 'Indicação do canal geral de suporte', 1),
    (5, 'Apoio na definição do escopo do suporte', 2),
    (6, 'Go-live assistido', 0),
    (6, 'Ajustes finais antes da disponibilização geral', 1),
    (6, 'Acompanhamento do Especialista SEI', 2),
    (7, 'Monitorar uso (métricas, gargalos)', 0),
    (7, 'Avaliar resultados (indicadores, feedback)', 1),
    (7, 'Propor melhorias (lições aprendidas, expansão)', 2);
