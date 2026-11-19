-- Adiciona suporte para dias da semana e intervalo de repetição em regras de agendamento

-- Criar tabela de dias da semana para regras de agendamento
CREATE TABLE IF NOT EXISTS regra_cadastro_dias_semana (
    regra_cadastro_id UUID NOT NULL,
    dia_semana VARCHAR(10) NOT NULL,
    FOREIGN KEY (regra_cadastro_id) REFERENCES regras_cadastro(id) 
        ON DELETE CASCADE,
    PRIMARY KEY (regra_cadastro_id, dia_semana),
    CONSTRAINT fk_regra_dia_semana_cadastro FOREIGN KEY (regra_cadastro_id) 
        REFERENCES regras_cadastro(id) ON DELETE CASCADE
);

-- Adicionar coluna de intervalo de repetição (em semanas) às regras de agendamento
ALTER TABLE regras_cadastro 
ADD COLUMN IF NOT EXISTS intervalo_repetacao_semanas INTEGER DEFAULT 1;

-- Adicionar índice para melhorar performance
CREATE INDEX IF NOT EXISTS idx_regra_dias_semana 
ON regra_cadastro_dias_semana(regra_cadastro_id);

-- Adicionar índice para melhorar performance de consultas por intervalo
CREATE INDEX IF NOT EXISTS idx_regra_intervalo_repetacao 
ON regras_cadastro(intervalo_repetacao_semanas);
