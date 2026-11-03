INSERT INTO tipo_veiculos (id, nome, capacidade)
SELECT UUID_TO_BIN(UUID()), 'Carro', 4
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1
    FROM tipo_veiculos
    WHERE LOWER(TRIM(nome)) = 'carro'
);
