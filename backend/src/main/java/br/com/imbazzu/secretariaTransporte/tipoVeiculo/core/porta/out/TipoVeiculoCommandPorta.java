package br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.porta.out;

import java.util.UUID;

import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.model.TipoVeiculo;

public interface TipoVeiculoCommandPorta {

    /**
     * Salva um tipo de veículo.
     * @param veiculo
     * @return
     */
    UUID salvar(TipoVeiculo veiculo);

    /**
     * Deleta um tipo de veículo.
     * @param id
     */
    void deletar(UUID id);


}