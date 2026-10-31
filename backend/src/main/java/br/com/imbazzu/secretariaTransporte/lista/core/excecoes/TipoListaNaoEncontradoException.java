package br.com.imbazzu.secretariaTransporte.lista.core.excecoes;

import java.util.UUID;

import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.EntidadeNaoEncontradoException;

public class TipoListaNaoEncontradoException extends EntidadeNaoEncontradoException{

    public TipoListaNaoEncontradoException(UUID id) {
    
        super("Id do Tipo da Lista não encontrado. ID: %s".formatted(id));
    }
    
}
