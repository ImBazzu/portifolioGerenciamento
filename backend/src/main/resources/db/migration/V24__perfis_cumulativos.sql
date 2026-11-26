UPDATE usuario_permissoes SET permissao = 'AGENDAMENTO' WHERE permissao = 'USUARIO';
ALTER TABLE usuarios ADD COLUMN versao_token BIGINT NOT NULL DEFAULT 0;
