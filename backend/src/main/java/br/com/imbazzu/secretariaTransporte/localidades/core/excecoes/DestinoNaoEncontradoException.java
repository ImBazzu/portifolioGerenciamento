package br.com.imbazzu.secretariaTransporte.localidades.core.excecoes;

import java.util.UUID;

import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.EntidadeNaoEncontradoException;

public class DestinoNaoEncontradoException extends EntidadeNaoEncontradoException {

    public DestinoNaoEncontradoException(UUID idDestino) {
        super("Id do Destino não encontrado\n Id: %s".formatted(idDestino));
    }

}
