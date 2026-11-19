-- Adicionar especificação e campos de busca/acompanhantes em pessoas
-- Primeiro, criar uma especificação padrão para dados existentes
INSERT INTO especificacoes (id, nome, prioridade, observacao, data_arquivado)
VALUES (UNHEX('00000000000000000000000000000001'), 'Padrão', 'MODERADA', 'Especificação padrão para migração', NULL);

-- Adicionar coluna especificacao_id e os novos campos
ALTER TABLE pessoas
    ADD COLUMN especificacao_id BINARY(16) NULL,
    ADD COLUMN buscar_na_residencia BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN quantidade_acompanhantes INT NOT NULL DEFAULT 0,
    ADD CONSTRAINT fk_pessoa_especificacao
        FOREIGN KEY (especificacao_id) REFERENCES especificacoes (id);

-- Atualizar pessoas existentes com a especificação padrão
UPDATE pessoas
SET especificacao_id = UNHEX('00000000000000000000000000000001')
WHERE especificacao_id IS NULL;

-- Tornar a coluna especificacao_id obrigatória após atualizar dados existentes
ALTER TABLE pessoas
    MODIFY COLUMN especificacao_id BINARY(16) NOT NULL;
