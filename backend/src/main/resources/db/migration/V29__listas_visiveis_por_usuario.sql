ALTER TABLE usuarios
    ADD COLUMN restricao_listas_agendamento BOOLEAN NOT NULL DEFAULT FALSE;

CREATE TABLE usuario_listas_agendamento (
    usuario_id BINARY(16) NOT NULL,
    lista_id BINARY(16) NOT NULL,
    CONSTRAINT pk_usuario_listas_agendamento PRIMARY KEY (usuario_id, lista_id),
    CONSTRAINT fk_usuario_listas_agendamento_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuarios (id) ON DELETE CASCADE,
    CONSTRAINT fk_usuario_listas_agendamento_lista
        FOREIGN KEY (lista_id) REFERENCES listas (id) ON DELETE CASCADE
);
