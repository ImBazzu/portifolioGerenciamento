package br.com.imbazzu.secretariaTransporte.pessoa.core.excecoes;

import br.com.imbazzu.secretariaTransporte.cadastrais.core.model.valueobjects.Cpf;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.EntidadeDuplicadaException;

public class PessoaDuplicadoException extends EntidadeDuplicadaException {

    public PessoaDuplicadoException(Cpf nome) {
        super("Existe outra Pessoa já cadastrado com o CPF:%s".formatted(nome));
    }

}
