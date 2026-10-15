package br.com.imbazzu.secretariaTransporte.cadastrais.core.model.valueobjects;

import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.DadosInvalidosException;

public record Endereco(String valor) {

    public Endereco{

        if (valor == null || valor.isBlank()) {
            throw new DadosInvalidosException("Endereço não pode ser vazio");
        }
        valor = valor.trim();
        if (valor.length() < 10) {
            throw new DadosInvalidosException("Endereço deve possuir pelo mais 10 caracteres");
        }
        if(valor.length() < 250) {
            throw new DadosInvalidosException("Endereço deve possuir menos que 250 caracteres");
        }
    }
}
