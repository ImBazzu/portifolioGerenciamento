package br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.model.valueobject;

import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.DadosInvalidosException;

public record NomeVeiculo(String valor) {

    public NomeVeiculo {
        if (valor == null || valor.isBlank()) {
            throw new DadosInvalidosException("Nome do veículo não pode ser vazio");
        }

        valor = valor.trim();

        if (valor.length() < 3) {
            throw new DadosInvalidosException(
                    "Nome do veículo deve possuir pelo menos 3 caracteres");
        }
        if (valor.length() >= 20) {
            throw new DadosInvalidosException(
                    "Nome do veículo deve possuir menos que 20 caracteres");
        }
    }

    @Override
    public final String toString() {
        return valor;
    }
}
