ALTER TABLE veiculos_agendamento
    ADD COLUMN finalidade_abertura VARCHAR(20) NOT NULL DEFAULT 'AJUSTE',
    ADD COLUMN lista_abertura_id BINARY(16) NULL;

ALTER TABLE veiculos_agendamento
    ADD CONSTRAINT fk_veiculo_lista_abertura
        FOREIGN KEY (lista_abertura_id) REFERENCES listas (id);
