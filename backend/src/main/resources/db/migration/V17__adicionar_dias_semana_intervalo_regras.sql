-- Adiciona suporte para dias da semana e intervalo de repetição em regras de agendamento

-- Criar tabela de dias da semana para regras de agendamento
CREATE TABLE regra_cadastro_dias_semana (
    regra_cadastro_id BINARY(16) NOT NULL,
    dia_semana VARCHAR(10) NOT NULL,
    PRIMARY KEY (regra_cadastro_id, dia_semana),
    CONSTRAINT fk_regra_dia_semana_cadastro FOREIGN KEY (regra_cadastro_id) 
        REFERENCES regras_cadastro(id) ON DELETE CASCADE
);

-- Adicionar coluna de intervalo de repetição (em semanas) às regras de agendamento
ALTER TABLE regras_cadastro 
ADD COLUMN intervalo_repeticao_semanas INTEGER NOT NULL DEFAULT 1,
ADD CONSTRAINT ck_regra_intervalo_repeticao
    CHECK (intervalo_repeticao_semanas BETWEEN 1 AND 52);
