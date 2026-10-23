CREATE TABLE listas (
    id              BINARY(16)   NOT NULL,
    nome            VARCHAR(59)  NOT NULL,
    descricao       VARCHAR(249) NULL,
    data_arquivado  DATE         NULL,

    CONSTRAINT pk_listas PRIMARY KEY (id)
);

CREATE TABLE regras_cadastro (
    id                  BINARY(16)   NOT NULL,
    lista_id            BINARY(16)   NOT NULL,
    obrigacao_anexo     BIT(1)       NOT NULL,
    descricao           VARCHAR(249) NULL,
    inicio_horario      TIME         NULL,
    final_horario       TIME         NULL,

    CONSTRAINT pk_regras_cadastro PRIMARY KEY (id),
    CONSTRAINT fk_regra_cadastro_lista FOREIGN KEY (lista_id)
        REFERENCES listas (id) ON DELETE CASCADE
);

CREATE TABLE regra_cadastro_prioridades (
    regra_cadastro_id   BINARY(16)   NOT NULL,
    prioridade          VARCHAR(20)  NOT NULL,

    CONSTRAINT pk_regra_cadastro_prioridades PRIMARY KEY (regra_cadastro_id, prioridade),
    CONSTRAINT fk_regra_prioridade_cadastro FOREIGN KEY (regra_cadastro_id)
        REFERENCES regras_cadastro (id) ON DELETE CASCADE
);

CREATE TABLE regra_cadastro_veiculos (
    regra_cadastro_id   BINARY(16) NOT NULL,
    veiculo_id          BINARY(16) NOT NULL,

    CONSTRAINT pk_regra_cadastro_veiculos PRIMARY KEY (regra_cadastro_id, veiculo_id),
    CONSTRAINT fk_regra_veiculo_cadastro FOREIGN KEY (regra_cadastro_id)
        REFERENCES regras_cadastro (id) ON DELETE CASCADE
);

CREATE TABLE regra_cadastro_cidades (
    regra_cadastro_id   BINARY(16) NOT NULL,
    cidade_id           BINARY(16) NOT NULL,

    CONSTRAINT pk_regra_cadastro_cidades PRIMARY KEY (regra_cadastro_id, cidade_id),
    CONSTRAINT fk_regra_cidade_cadastro FOREIGN KEY (regra_cadastro_id)
        REFERENCES regras_cadastro (id) ON DELETE CASCADE
);

CREATE TABLE regras_separacao (
    id                      BINARY(16) NOT NULL,
    lista_id                BINARY(16) NOT NULL,
    tipo_veiculo_id         BINARY(16) NOT NULL,
    tempo_tolerancia_segundos BIGINT   NOT NULL,
    tempo_tolerancia_nanos  INT        NOT NULL,

    CONSTRAINT pk_regras_separacao PRIMARY KEY (id),
    CONSTRAINT fk_regra_separacao_lista FOREIGN KEY (lista_id)
        REFERENCES listas (id) ON DELETE CASCADE,
    CONSTRAINT ck_regra_separacao_tolerancia CHECK (
        tempo_tolerancia_segundos >= 0
        AND tempo_tolerancia_nanos BETWEEN 0 AND 999999999
    )
);
