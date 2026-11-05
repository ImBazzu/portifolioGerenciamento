package br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.service;

import java.util.UUID;

import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.excecoes.TipoVeiculoNaoEncontradoException;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.model.TipoVeiculo;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.porta.out.TipoVeiculoQueryPorta;

public class TipoVeiculoServiceBuscarEntidade {
    
    private final TipoVeiculoQueryPorta query;

    public TipoVeiculoServiceBuscarEntidade(TipoVeiculoQueryPorta query) {
        this.query = query;
    }

    /**
     * Busca a entidade TipoVeiculo no banco de dados pelo id, caso não exista lança exceção
     * @param id
     * @return
     */
    public TipoVeiculo executar(UUID id) {
        //Busca a entidade no banco de dados, caso não exista lança exceção
        return query.buscarEntidadePorId(id)
                .orElseThrow(
                    ()->new TipoVeiculoNaoEncontradoException(id));    
                }

    
}
