package br.com.imbazzu.frontSpring.veiculo.core.excecoes;

import java.util.UUID;

public class VeiculoDuplicadoException extends RuntimeException {

    private UUID id;

    public VeiculoDuplicadoException(UUID id) {
        super("Nome de veículo já cadastrado");
        this.id = id;
    }

    public UUID getId() {
        return id;
    }
}
