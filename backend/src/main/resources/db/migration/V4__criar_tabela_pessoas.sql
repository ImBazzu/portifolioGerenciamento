CREATE TABLE pessoas (
    id                   BINARY(16)   NOT NULL,
    nome                 VARCHAR(150) NOT NULL,
    cpf                  VARCHAR(11)  NOT NULL,
    telefone             VARCHAR(11) NOT NULL,
    endereco             VARCHAR(250) NOT NULL,
    arquivado            TINYINT(1)   NOT NULL DEFAULT 0,
    pessoa_condicao_id   BINARY(16)   NULL,

    CONSTRAINT pk_pessoas PRIMARY KEY (id),
    CONSTRAINT uk_pessoas_cpf UNIQUE (cpf),
    CONSTRAINT fk_pessoas_pessoa_condicao
        FOREIGN KEY (pessoa_condicao_id) REFERENCES pessoa_condicoes (id)
);
