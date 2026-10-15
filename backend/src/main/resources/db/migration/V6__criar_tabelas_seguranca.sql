CREATE TABLE usuarios (
    id     BINARY(16)   NOT NULL,
    nome   VARCHAR(100) NOT NULL,
    senha  VARCHAR(255) NOT NULL,

    CONSTRAINT pk_usuarios PRIMARY KEY (id),
    CONSTRAINT uk_usuario_nome UNIQUE (nome)
);

-- Tabela auxiliar gerada pelo @ElementCollection em UsuarioJpaEntity
-- Sem esta tabela o Hibernate falha ao subir (Flyway roda antes do DDL automático)
CREATE TABLE usuario_permissoes (
    usuario_id  BINARY(16)  NOT NULL,
    permissao   VARCHAR(50) NOT NULL,

    CONSTRAINT fk_usuario_permissoes_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuarios (id)
);

-- Usuário admin padrão
INSERT INTO usuarios (id, nome, senha)
VALUES (UUID_TO_BIN(UUID()), 'admin', '$2a$12$dBJ/a7S/gN.EY9NR.gfwfeL3QX3RPVNfiWCFw64uH0QL0GMbHzQ5C');

INSERT INTO usuario_permissoes (usuario_id, permissao)
SELECT id, 'ADMIN' FROM usuarios WHERE nome = 'admin';
