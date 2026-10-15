package br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.excecoes;

import java.util.UUID;

import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.EntidadeNaoEncontradoException;

public class TipoVeiculoNaoEncontradoException extends EntidadeNaoEncontradoException {

    public TipoVeiculoNaoEncontradoException(UUID id) {
        super("Id do veículo não encontrado.\nId: %s".formatted(id));
    }

}
