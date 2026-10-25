CREATE TABLE dias_agendamento (
    data       DATE         NOT NULL,
    aberto_em  DATETIME(6)  NOT NULL,

    CONSTRAINT pk_dias_agendamento PRIMARY KEY (data)
);

CREATE TABLE veiculos_agendamento (
    id               BINARY(16)   NOT NULL,
    data             DATE         NOT NULL,
    tipo_veiculo_id  BINARY(16)   NOT NULL,
    nome_tipo_veiculo VARCHAR(100) NOT NULL,
    capacidade       INT          NOT NULL,
    identificacao    VARCHAR(80)  NULL,
    hora_saida       TIME         NULL,
    hora_saida_calculada BIT(1)   NOT NULL,

    CONSTRAINT pk_veiculos_agendamento PRIMARY KEY (id),
    CONSTRAINT fk_veiculo_agendamento_dia FOREIGN KEY (data)
        REFERENCES dias_agendamento (data) ON DELETE CASCADE,
    CONSTRAINT fk_veiculo_agendamento_tipo FOREIGN KEY (tipo_veiculo_id)
        REFERENCES tipo_veiculos (id)
);

CREATE TABLE agendamentos (
    id                    BINARY(16)  NOT NULL,
    data                  DATE        NOT NULL,
    pessoa_id             BINARY(16)  NOT NULL,
    lista_id              BINARY(16)  NOT NULL,
    regra_cadastro_id     BINARY(16)  NOT NULL,
    tipo_veiculo_id       BINARY(16)  NOT NULL,
    destino_id            BINARY(16)  NOT NULL,
    tempo_viagem_segundos BIGINT      NOT NULL,
    tempo_viagem_nanos    INT         NOT NULL,
    tolerancia_segundos   BIGINT      NOT NULL,
    tolerancia_nanos      INT         NOT NULL,
    hora_chegada          TIME        NOT NULL,
    buscar_na_residencia  BIT(1)      NOT NULL,
    acompanhantes        INT         NOT NULL DEFAULT 0,
    veiculo_agendamento_id BINARY(16) NULL,

    CONSTRAINT pk_agendamentos PRIMARY KEY (id),
    CONSTRAINT uk_agendamento_pessoa_dia UNIQUE (data, pessoa_id),
    CONSTRAINT ck_agendamento_acompanhantes CHECK (acompanhantes >= 0),
    CONSTRAINT ck_agendamento_tempos CHECK (
        tempo_viagem_segundos >= 0
        AND tempo_viagem_nanos BETWEEN 0 AND 999999999
        AND tolerancia_segundos >= 0
        AND tolerancia_nanos BETWEEN 0 AND 999999999
    ),
    CONSTRAINT fk_agendamento_dia FOREIGN KEY (data)
        REFERENCES dias_agendamento (data) ON DELETE CASCADE,
    CONSTRAINT fk_agendamento_pessoa FOREIGN KEY (pessoa_id)
        REFERENCES pessoas (id),
    CONSTRAINT fk_agendamento_lista FOREIGN KEY (lista_id)
        REFERENCES listas (id),
    CONSTRAINT fk_agendamento_tipo FOREIGN KEY (tipo_veiculo_id)
        REFERENCES tipo_veiculos (id),
    CONSTRAINT fk_agendamento_destino FOREIGN KEY (destino_id)
        REFERENCES destinos (id),
    CONSTRAINT fk_agendamento_veiculo_aberto FOREIGN KEY (veiculo_agendamento_id)
        REFERENCES veiculos_agendamento (id)
);
