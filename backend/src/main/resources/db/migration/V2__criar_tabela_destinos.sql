CREATE TABLE destinos (
    id          BINARY(16)   NOT NULL,
    nome        VARCHAR(150) NOT NULL,
    cidade_id   BINARY(16)   NOT NULL,
    arquivado   TINYINT(1)   NOT NULL DEFAULT 0,

    CONSTRAINT pk_destinos PRIMARY KEY (id),
    CONSTRAINT uk_destinos_nome_cidade UNIQUE (nome, cidade_id),
    CONSTRAINT fk_destinos_cidade
        FOREIGN KEY (cidade_id) REFERENCES cidades (id)
);
