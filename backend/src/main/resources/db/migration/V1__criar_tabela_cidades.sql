CREATE TABLE cidades (
    id          BINARY(16)   NOT NULL,
    nome        VARCHAR(100) NOT NULL,
    estado      VARCHAR(2)   NOT NULL,
    tempo_viagem BIGINT      NOT NULL,
    data_arquivado DATE      NULL,

    CONSTRAINT pk_cidades PRIMARY KEY (id),
    CONSTRAINT uk_cidade_nome_estado UNIQUE (nome, estado)
);
