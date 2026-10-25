package br.com.imbazzu.frontSpring.pessoa.core.excecoes;

import java.util.UUID;

public class PessoaNaoEncontradaException extends RuntimeException {
    public PessoaNaoEncontradaException(UUID id) {
        super("Pessoa não encontrada: " + id);
    }
}
