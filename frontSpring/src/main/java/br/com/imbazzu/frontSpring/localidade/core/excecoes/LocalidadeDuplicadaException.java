package br.com.imbazzu.frontSpring.localidade.core.excecoes;

import java.util.UUID;

public class LocalidadeDuplicadaException extends RuntimeException {

    public LocalidadeDuplicadaException(UUID id) {
        super("Nome já cadastrado para esta localidade. ID: " + id);
    }
}
