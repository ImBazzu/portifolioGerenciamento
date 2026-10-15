CREATE TABLE tipo_veiculos (
    id          BINARY(16)   NOT NULL,
    nome        VARCHAR(100) NOT NULL,
    capacidade   INT NOT NULL,
    data_arquivado  DATE NULL,

    CONSTRAINT pk_veiculos PRIMARY KEY (id),
    CONSTRAINT uk_veiculos_nome UNIQUE (nome)
);

