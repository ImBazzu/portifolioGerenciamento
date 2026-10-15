package br.com.imbazzu.secretariaTransporte.localidades.core.excecoes;

import java.util.UUID;

import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.EntidadeNaoEncontradoException;

public class CidadeNaoEncontradaException extends EntidadeNaoEncontradoException {

    public CidadeNaoEncontradaException(UUID idCidade) {
        super("Id da Cidade não encontrado\n Id: %s".formatted(idCidade));
    }

}
