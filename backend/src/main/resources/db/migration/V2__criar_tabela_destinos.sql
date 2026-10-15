CREATE TABLE destinos (
    id          BINARY(16)   NOT NULL,
    nome        VARCHAR(100) NOT NULL,
    cidade_id   BINARY(16)   NOT NULL,
    data_arquivado DATE      NULL,

    CONSTRAINT pk_destinos PRIMARY KEY (id),
    CONSTRAINT uk_destinos_nome_cidade UNIQUE (nome, cidade_id),
    CONSTRAINT fk_destinos_cidade
        FOREIGN KEY (cidade_id) REFERENCES cidades (id)
);
