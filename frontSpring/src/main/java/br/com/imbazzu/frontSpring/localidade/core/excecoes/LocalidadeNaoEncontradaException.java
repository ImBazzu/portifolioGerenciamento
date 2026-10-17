package br.com.imbazzu.frontSpring.localidade.core.excecoes;

import java.util.UUID;

public class LocalidadeNaoEncontradaException extends RuntimeException {

    public LocalidadeNaoEncontradaException(UUID id) {
        super("Localidade não encontrada. ID: " + id);
    }
}
