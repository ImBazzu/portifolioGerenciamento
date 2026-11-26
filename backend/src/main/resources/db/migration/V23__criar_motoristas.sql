CREATE TABLE categorias_motorista (
    id BINARY(16) NOT NULL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    CONSTRAINT uk_categoria_motorista_nome UNIQUE (nome)
);

CREATE TABLE motoristas (
    id BINARY(16) NOT NULL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    telefone VARCHAR(11) NOT NULL,
    categoria_id BINARY(16) NOT NULL,
    CONSTRAINT uk_motorista_telefone UNIQUE (telefone),
    CONSTRAINT fk_motorista_categoria FOREIGN KEY (categoria_id) REFERENCES categorias_motorista(id)
);

ALTER TABLE veiculos_agendamento ADD COLUMN motorista_id BINARY(16) NULL;
ALTER TABLE veiculos_agendamento ADD CONSTRAINT fk_veiculo_motorista
    FOREIGN KEY (motorista_id) REFERENCES motoristas(id);
