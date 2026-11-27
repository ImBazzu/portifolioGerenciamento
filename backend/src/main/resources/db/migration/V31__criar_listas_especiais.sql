ALTER TABLE listas ADD COLUMN finalidade_especial VARCHAR(20) NULL;
ALTER TABLE listas ADD CONSTRAINT uk_listas_finalidade_especial UNIQUE (finalidade_especial);

SET @alta = (SELECT id FROM listas WHERE data_arquivado IS NULL AND LOWER(TRIM(nome)) = 'alta' ORDER BY id LIMIT 1);
SET @emergencia = (SELECT id FROM listas WHERE data_arquivado IS NULL AND REPLACE(LOWER(TRIM(nome)), 'ê', 'e') = 'emergencia' ORDER BY id LIMIT 1);
SET @nova_alta = @alta IS NULL;
SET @nova_emergencia = @emergencia IS NULL;
SET @alta = COALESCE(@alta, UUID_TO_BIN(UUID()));
SET @emergencia = COALESCE(@emergencia, UUID_TO_BIN(UUID()));

INSERT INTO listas (id, nome, descricao, finalidade_especial)
SELECT @alta, 'Alta', 'Atendimento de alta hospitalar', 'ALTA' WHERE @nova_alta;
INSERT INTO listas (id, nome, descricao, finalidade_especial)
SELECT @emergencia, 'Emergência', 'Atendimento de emergência', 'EMERGENCIA' WHERE @nova_emergencia;
UPDATE listas SET finalidade_especial = 'ALTA' WHERE id = @alta;
UPDATE listas SET finalidade_especial = 'EMERGENCIA' WHERE id = @emergencia;

SET @regra_alta = UUID_TO_BIN(UUID());
SET @regra_emergencia = UUID_TO_BIN(UUID());
INSERT INTO regras_cadastro (id, lista_id, obrigacao_anexo, descricao)
SELECT @regra_alta, @alta, 0, 'Regra inicial de atendimento'
WHERE @nova_alta AND EXISTS (SELECT 1 FROM tipo_veiculos WHERE data_arquivado IS NULL)
  AND EXISTS (SELECT 1 FROM prioridades);
INSERT INTO regras_cadastro (id, lista_id, obrigacao_anexo, descricao)
SELECT @regra_emergencia, @emergencia, 0, 'Regra inicial de atendimento'
WHERE @nova_emergencia AND EXISTS (SELECT 1 FROM tipo_veiculos WHERE data_arquivado IS NULL)
  AND EXISTS (SELECT 1 FROM prioridades);

INSERT INTO regra_cadastro_prioridades (regra_cadastro_id, prioridade_id)
SELECT r.id, p.id FROM regras_cadastro r CROSS JOIN prioridades p
WHERE r.id IN (@regra_alta, @regra_emergencia);
INSERT INTO regra_cadastro_veiculos (regra_cadastro_id, veiculo_id)
SELECT r.id, v.id FROM regras_cadastro r CROSS JOIN tipo_veiculos v
WHERE r.id IN (@regra_alta, @regra_emergencia) AND v.data_arquivado IS NULL;
INSERT INTO regras_separacao (id, lista_id, tipo_veiculo_id, tempo_tolerancia_segundos, tempo_tolerancia_nanos)
SELECT UUID_TO_BIN(UUID()), l.id, v.id, 0, 0
FROM listas l CROSS JOIN tipo_veiculos v
WHERE ((l.id = @alta AND @nova_alta) OR (l.id = @emergencia AND @nova_emergencia))
  AND LOWER(TRIM(v.nome)) = 'carro' AND v.data_arquivado IS NULL;
