CREATE TABLE cidades (
    id          BINARY(16)   NOT NULL,
    nome        VARCHAR(100) NOT NULL,
    estado      VARCHAR(2)   NOT NULL,
    arquivado   TINYINT(1)   NOT NULL DEFAULT 0,

    CONSTRAINT pk_cidades PRIMARY KEY (id),
    CONSTRAINT uk_cidades_nome_estado UNIQUE (nome, estado)
);
