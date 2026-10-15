package br.com.imbazzu.frontspring.veiculo.core.excecoes;

import java.util.UUID;

public class VeiculoNaoEncontradoException extends RuntimeException {
    public VeiculoNaoEncontradoException(UUID id) {
        super("Veiculo não encontrado. ID: "+id);
    }
}
