package br.com.imbazzu.secretariaTransporte.cadastrais.core.model.valueobjects;

import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.DadosInvalidosException;

public record Cpf(String valor) {

    public Cpf{
        var cpfSemPontos = valor.replaceAll("[^0-9]", "");
        if (!validarCpf(cpfSemPontos))
            throw new DadosInvalidosException("CPF inválido");
        valor = cpfSemPontos;
    }



    private static boolean validarCpf(String cpf) {
        if (cpf.length() != 11) return false;
        if (cpf.matches("(\\d)\\1{10}")) return false;

        int soma = 0;
        for (int i = 0; i < 9; i++) soma += (cpf.charAt(i) - '0') * (10 - i);
        int d1 = 11 - (soma % 11);
        if (d1 >= 10) d1 = 0;
        if ((cpf.charAt(9) - '0') != d1) return false;

        soma = 0;
        for (int i = 0; i < 10; i++) soma += (cpf.charAt(i) - '0') * (11 - i);
        int d2 = 11 - (soma % 11);
        if (d2 >= 10) d2 = 0;
        return (cpf.charAt(10) - '0') == d2;
    }
}
