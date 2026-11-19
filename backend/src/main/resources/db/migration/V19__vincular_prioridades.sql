ALTER TABLE especificacoes ADD COLUMN prioridade_id BINARY(16) NULL;
UPDATE especificacoes e
JOIN prioridades p ON p.nome = e.prioridade
SET e.prioridade_id = p.id;
ALTER TABLE especificacoes MODIFY COLUMN prioridade_id BINARY(16) NOT NULL;
ALTER TABLE especificacoes
    ADD CONSTRAINT fk_especificacao_prioridade FOREIGN KEY (prioridade_id) REFERENCES prioridades (id);

ALTER TABLE regra_cadastro_prioridades ADD COLUMN prioridade_id BINARY(16) NULL;
UPDATE regra_cadastro_prioridades r
JOIN prioridades p ON p.nome = r.prioridade
SET r.prioridade_id = p.id;
ALTER TABLE regra_cadastro_prioridades MODIFY COLUMN prioridade_id BINARY(16) NOT NULL;
ALTER TABLE regra_cadastro_prioridades ADD INDEX ix_regra_prioridade_regra (regra_cadastro_id);
ALTER TABLE regra_cadastro_prioridades
    DROP PRIMARY KEY,
    ADD PRIMARY KEY (regra_cadastro_id, prioridade_id),
    ADD CONSTRAINT fk_regra_prioridade FOREIGN KEY (prioridade_id) REFERENCES prioridades (id);

ALTER TABLE especificacoes DROP COLUMN prioridade;
ALTER TABLE regra_cadastro_prioridades DROP COLUMN prioridade;
