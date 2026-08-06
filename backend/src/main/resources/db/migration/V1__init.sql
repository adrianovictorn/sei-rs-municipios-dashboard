CREATE TABLE municipio (
    id                       BIGSERIAL PRIMARY KEY,
    nome                     VARCHAR(150)  NOT NULL,
    codigo_ibge              VARCHAR(10),
    regiao                   VARCHAR(100),
    populacao                INTEGER,
    patrocinador_executivo   VARCHAR(150),
    ponto_focal_nome         VARCHAR(150),
    ponto_focal_email        VARCHAR(150),
    ponto_focal_telefone     VARCHAR(30),
    data_inicio              DATE,
    observacoes              TEXT,
    created_at               TIMESTAMP NOT NULL DEFAULT now(),
    updated_at               TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE etapa (
    id            BIGSERIAL PRIMARY KEY,
    municipio_id  BIGINT NOT NULL REFERENCES municipio(id) ON DELETE CASCADE,
    nome          VARCHAR(150) NOT NULL,
    descricao     VARCHAR(500),
    ordem         INTEGER NOT NULL DEFAULT 0
);

CREATE INDEX idx_etapa_municipio ON etapa(municipio_id);

CREATE TABLE checklist_item (
    id              BIGSERIAL PRIMARY KEY,
    etapa_id        BIGINT NOT NULL REFERENCES etapa(id) ON DELETE CASCADE,
    descricao       VARCHAR(500) NOT NULL,
    concluido       BOOLEAN NOT NULL DEFAULT FALSE,
    data_conclusao  DATE,
    ordem           INTEGER NOT NULL DEFAULT 0
);

CREATE INDEX idx_checklist_item_etapa ON checklist_item(etapa_id);
