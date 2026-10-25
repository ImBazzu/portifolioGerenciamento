ALTER TABLE pessoas
    ADD COLUMN observacao VARCHAR(249) NULL,
    ADD COLUMN prioridade VARCHAR(20) NOT NULL DEFAULT 'MODERADA',
    ADD COLUMN data_arquivado DATE NULL;

UPDATE pessoas
SET data_arquivado = CURRENT_DATE
WHERE arquivado = 1;
