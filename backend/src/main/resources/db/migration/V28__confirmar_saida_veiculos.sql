ALTER TABLE veiculos_agendamento
    ADD COLUMN saida_confirmada_em DATETIME(6) NULL,
    ADD COLUMN saida_confirmada_por_id BINARY(16) NULL;

ALTER TABLE veiculos_agendamento
    ADD CONSTRAINT fk_veiculo_saida_confirmada_por
        FOREIGN KEY (saida_confirmada_por_id) REFERENCES usuarios (id);
