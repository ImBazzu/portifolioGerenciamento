package br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.excecoes;

import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.EntidadeEmUsoException;

public class TipoVeiculoEmUsoException extends EntidadeEmUsoException {

    public TipoVeiculoEmUsoException() {
        //Operação não permitida. O tipo de veículo está em uso.
        super("Operação não permitida. O tipo de veículo está em uso.");
    }
    
}
