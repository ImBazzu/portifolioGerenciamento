CREATE TABLE veiculos (
    id BINARY(16) NOT NULL,
    id_tipo_veiculo BINARY(16) NOT NULL,
    data_saida DATE NOT NULL,
    hora_saida TIME NOT NULL,
    qtd_passageiro INT NOT NULL,

    CONSTRAINT pk_veiculos PRIMARY KEY (id),

    CONSTRAINT fk_veiculos_tipo_veiculo
        FOREIGN KEY (id_tipo_veiculo)
        REFERENCES tipo_veiculos (id),

    INDEX idx_veiculos_tipo_veiculo_data
        (id_tipo_veiculo, data_saida)
);