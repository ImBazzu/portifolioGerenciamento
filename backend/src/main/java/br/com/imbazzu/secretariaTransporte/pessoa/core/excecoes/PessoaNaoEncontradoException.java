package br.com.imbazzu.secretariaTransporte.pessoa.core.excecoes;

import java.util.UUID;

import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.EntidadeNaoEncontradoException;

public class PessoaNaoEncontradoException extends EntidadeNaoEncontradoException {

    public PessoaNaoEncontradoException(UUID id) {
        super("Id de Pessoa não encontrado.\nId: %s".formatted(id));
    }

}
