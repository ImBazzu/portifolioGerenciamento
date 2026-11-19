CREATE TABLE prioridades (
    id                       BINARY(16)   NOT NULL,
    nome                     VARCHAR(100) NOT NULL,
    buscar_na_residencia     BOOLEAN      NOT NULL DEFAULT FALSE,
    quantidade_acompanhantes INT          NOT NULL DEFAULT 0,
    CONSTRAINT pk_prioridades PRIMARY KEY (id),
    CONSTRAINT uk_prioridade_nome UNIQUE (nome),
    CONSTRAINT ck_prioridade_acompanhantes CHECK (quantidade_acompanhantes >= 0)
);

INSERT INTO prioridades (id, nome, buscar_na_residencia, quantidade_acompanhantes) VALUES
    (UNHEX('00000000000000000000000000000001'), 'BAIXA', FALSE, 0),
    (UNHEX('00000000000000000000000000000002'), 'MODERADA', FALSE, 0),
    (UNHEX('00000000000000000000000000000003'), 'ALTA', FALSE, 0),
    (UNHEX('00000000000000000000000000000004'), 'URGENTE', FALSE, 0);
