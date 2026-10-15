package br.com.imbazzu.secretariaTransporte.cadastrais.core.model.valueobjects;

import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.DadosInvalidosException;

import java.util.regex.Pattern;

public record Telefone(String valor) {

    public Telefone{
        if (!Pattern.matches("^\\(?\\d{2}\\)?\\s?\\d{1}\\s?\\d{4}-?\\d{4}$", valor)) {
            throw new DadosInvalidosException("Formato de telefone inválido. Use (DD)9XXXX-XXXX");
        }
        valor = valor.replaceAll("[^0-9]", "");
    }
}
