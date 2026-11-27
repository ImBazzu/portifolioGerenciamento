ALTER TABLE agendamentos
    ADD COLUMN cancelado_em DATETIME(6) NULL,
    ADD COLUMN motivo_cancelamento VARCHAR(500) NULL,
    ADD COLUMN cancelado_por_id BINARY(16) NULL;

ALTER TABLE agendamentos
    ADD CONSTRAINT fk_agendamento_cancelado_por
        FOREIGN KEY (cancelado_por_id) REFERENCES usuarios (id);

CREATE INDEX idx_agendamentos_data_cancelado
    ON agendamentos (data, cancelado_em);
