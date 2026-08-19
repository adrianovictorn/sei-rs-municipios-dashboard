ALTER TABLE municipio
    ADD COLUMN data_prevista_golive DATE;

CREATE TABLE tipo_agenda (
    id    BIGSERIAL PRIMARY KEY,
    nome  VARCHAR(150) NOT NULL
);

CREATE TABLE agenda (
    id              BIGSERIAL PRIMARY KEY,
    municipio_id    BIGINT NOT NULL REFERENCES municipio(id) ON DELETE CASCADE,
    tipo_agenda_id  BIGINT REFERENCES tipo_agenda(id) ON DELETE SET NULL,
    titulo          VARCHAR(200) NOT NULL,
    data_hora       TIMESTAMP NOT NULL,
    local           VARCHAR(300),
    observacoes     TEXT,
    realizada       BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_agenda_municipio ON agenda(municipio_id);
CREATE INDEX idx_agenda_tipo ON agenda(tipo_agenda_id);

-- Dado inicial, totalmente editavel/removivel depois pela tela "Agendas" -> "Gerenciar
-- tipos" -- nada disso fica hardcoded em codigo Java/TS.
INSERT INTO tipo_agenda (nome) VALUES
    ('Comunicação'),
    ('SEI Usar'),
    ('SEI Administrar');
