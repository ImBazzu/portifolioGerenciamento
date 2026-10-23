package br.com.imbazzu.secretariaTransporte.cadastrais.core.model.valueobjects;

import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.DadosInvalidosException;

public record Observacao(String valor) {

    public Observacao{

        if (valor == null || valor.isBlank()) {
            throw new DadosInvalidosException("Observação não pode ser vazio");
        }
        valor = valor.trim();
        if (valor.length() < 5) {
            throw new DadosInvalidosException("Observação deve possuir pelo menos 5 caracteres");
        }
        if (valor.length() >= 250) {
            throw new DadosInvalidosException("Observação deve possuir menos que 250 caracteres");
        }
    }
}
