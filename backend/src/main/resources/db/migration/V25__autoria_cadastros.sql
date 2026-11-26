ALTER TABLE pessoas
    ADD COLUMN autor_id BINARY(16) NULL,
    ADD CONSTRAINT fk_pessoa_autor FOREIGN KEY (autor_id) REFERENCES usuarios (id);

ALTER TABLE agendamentos
    ADD COLUMN autor_id BINARY(16) NULL,
    ADD CONSTRAINT fk_agendamento_autor FOREIGN KEY (autor_id) REFERENCES usuarios (id);
