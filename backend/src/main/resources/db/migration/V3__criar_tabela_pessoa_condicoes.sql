CREATE TABLE pessoa_condicoes (
    id          BINARY(16)   NOT NULL,
    nome        VARCHAR(100) NOT NULL,
    arquivado   TINYINT(1)   NOT NULL DEFAULT 0,

    CONSTRAINT pk_pessoa_condicoes PRIMARY KEY (id),
    CONSTRAINT uk_pessoa_condicoes_nome UNIQUE (nome)
);
