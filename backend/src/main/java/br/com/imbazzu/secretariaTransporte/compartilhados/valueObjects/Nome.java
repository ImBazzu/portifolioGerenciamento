package br.com.imbazzu.secretariaTransporte.compartilhados.valueObjects;

import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.DadosInvalidosException;

public record Nome(String valor) {

    public Nome {
        if (valor == null || valor.isBlank()) {
            throw new DadosInvalidosException("Nome não pode ser vazio");
        }

        valor = valor.trim();

        if (valor.length() < 3) {
            throw new DadosInvalidosException(
                    "Nome deve possuir pelo menos 3 caracteres"
            );
        }
        if (valor.length() >= 60) {
            throw new DadosInvalidosException(
                    "Nome deve possuir menos que 60 caracteres"
            );
        }
    }

}
