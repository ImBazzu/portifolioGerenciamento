package br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.service;

import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.excecoes.TipoVeiculoEmUsoException;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.model.TipoVeiculo;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.porta.out.TipoVeiculoQueryPorta;

public class TipoVeiculoServiceValidarExclusao {
    
    private final TipoVeiculoQueryPorta tipoVeiculoQuery;

    public TipoVeiculoServiceValidarExclusao(TipoVeiculoQueryPorta tipoVeiculoQuery) {
        this.tipoVeiculoQuery = tipoVeiculoQuery;
    }

    /**
     * Valida se o tipo de veículo pode ser excluído, caso não possa lança exceção.
     * Valida se existem veículos associados ao tipo de veículo, caso existam lança exceção
     * @param tipoVeiculo
     */
    public void executar(TipoVeiculo tipoVeiculo) {
        // Valida se o tipo de veículo pode ser excluído, caso não possa lança exceção
        tipoVeiculo.validarExclusao();
        // Valida se existem veículos associados ao tipo de veículo, caso existam lança exceção
        if (tipoVeiculoQuery.existeVeiculoUsandoTipoVeiculo(tipoVeiculo.getId())) {
            throw new TipoVeiculoEmUsoException();
        }
    }

}
