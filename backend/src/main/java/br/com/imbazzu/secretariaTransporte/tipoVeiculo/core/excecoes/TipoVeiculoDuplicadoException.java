package br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.excecoes;

import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.EntidadeDuplicadaException;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.model.valueobject.NomeVeiculo;

public class TipoVeiculoDuplicadoException extends EntidadeDuplicadaException {

    public TipoVeiculoDuplicadoException(NomeVeiculo nome) {
    //Construtor da exceção que recebe o nome do veículo duplicado e formata a mensagem de erro
        super("Existe outro veículo já cadastrado com %s".formatted(nome));
    }

}
