CREATE TABLE passageiros (
    id              BINARY(16) NOT NULL,
    destino_id      BINARY(16) NOT NULL,
    veiculo_id      BINARY(16) NOT NULL,
    acompanhantes   INT         NOT NULL,
    hora_chegada    TIME        NOT NULL,
    buscar          BIT(1)      NOT NULL,
    status          VARCHAR(20) NOT NULL,

    CONSTRAINT pk_passageiros PRIMARY KEY (id)
);
