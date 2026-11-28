SET @encaixe = (SELECT id FROM listas WHERE data_arquivado IS NULL
    AND LOWER(TRIM(nome)) = 'encaixe' AND finalidade_especial IS NULL ORDER BY id LIMIT 1);
SET @novo_encaixe = @encaixe IS NULL;
SET @encaixe = COALESCE(@encaixe, UUID_TO_BIN(UUID()));

INSERT INTO listas (id, nome, descricao, finalidade_especial)
SELECT @encaixe, 'Encaixe', 'Inserção manual em veículos', 'ENCAIXE' WHERE @novo_encaixe;
UPDATE listas SET finalidade_especial = 'ENCAIXE' WHERE id = @encaixe;

SET @regra_encaixe = UUID_TO_BIN(UUID());
INSERT INTO regras_cadastro (id, lista_id, obrigacao_anexo, descricao)
SELECT @regra_encaixe, @encaixe, 0, 'Regra inicial de encaixe'
WHERE @novo_encaixe AND EXISTS (SELECT 1 FROM tipo_veiculos WHERE data_arquivado IS NULL)
  AND EXISTS (SELECT 1 FROM prioridades);

INSERT INTO regra_cadastro_prioridades (regra_cadastro_id, prioridade_id)
SELECT @regra_encaixe, p.id FROM prioridades p
WHERE EXISTS (SELECT 1 FROM regras_cadastro WHERE id = @regra_encaixe);
INSERT INTO regra_cadastro_veiculos (regra_cadastro_id, veiculo_id)
SELECT @regra_encaixe, v.id FROM tipo_veiculos v
WHERE v.data_arquivado IS NULL AND EXISTS (SELECT 1 FROM regras_cadastro WHERE id = @regra_encaixe);
INSERT INTO regras_separacao (id, lista_id, tipo_veiculo_id, tempo_tolerancia_segundos, tempo_tolerancia_nanos)
SELECT UUID_TO_BIN(UUID()), @encaixe, v.id, 0, 0
FROM tipo_veiculos v
WHERE @novo_encaixe AND LOWER(TRIM(v.nome)) = 'carro' AND v.data_arquivado IS NULL;
