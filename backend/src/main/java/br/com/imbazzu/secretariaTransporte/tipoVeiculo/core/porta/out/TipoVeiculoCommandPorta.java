package br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.porta.out;

import java.util.UUID;

import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.model.TipoVeiculo;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.model.valueobject.NomeVeiculo;

public interface TipoVeiculoCommandPorta {

    UUID salvar(TipoVeiculo veiculo);

    TipoVeiculo buscarPorId(UUID id);

    void deletar(UUID id);

    boolean verificarPorNome(NomeVeiculo nome);

    boolean verificarPorNomeIgnorandoId(UUID id, NomeVeiculo nome);
}