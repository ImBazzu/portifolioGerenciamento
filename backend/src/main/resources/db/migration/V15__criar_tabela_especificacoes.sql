-- Criar tabela de especificações
CREATE TABLE especificacoes (
    id                BINARY(16)    NOT NULL,
    nome              VARCHAR(100)  NOT NULL UNIQUE,
    prioridade        VARCHAR(20)   NOT NULL,
    observacao        VARCHAR(249)  NULL,
    data_arquivado    DATE          NULL,

    CONSTRAINT pk_especificacoes PRIMARY KEY (id),
    CONSTRAINT uk_especificacao_nome UNIQUE (nome)
);
