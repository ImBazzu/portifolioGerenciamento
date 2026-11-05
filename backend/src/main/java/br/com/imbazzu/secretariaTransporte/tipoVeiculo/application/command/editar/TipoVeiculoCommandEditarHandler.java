package br.com.imbazzu.secretariaTransporte.tipoVeiculo.application.command.editar;

import org.springframework.stereotype.Service;

import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.excecoes.TipoVeiculoDuplicadoException;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.model.valueobject.NomeVeiculo;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.porta.out.TipoVeiculoCommandPorta;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.service.TipoVeiculoServiceBuscarEntidade;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.service.TipoVeiculoServiceEditarEntidade;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TipoVeiculoCommandEditarHandler {

    private final TipoVeiculoCommandPorta commandPorta;

    private final TipoVeiculoServiceBuscarEntidade serviceBuscarEntidade;

    private final TipoVeiculoServiceEditarEntidade serviceEditarEntidade;

    public void handler(TipoVeiculoCommandEditar dto) {
        // Buscar a entidade existente pelo ID
        var tipoVeiculo = serviceBuscarEntidade.executar(dto.id());
        // Editar a entidade com os novos valores do DTO
        serviceEditarEntidade.executar(tipoVeiculo, dto.id(), dto.nome(), dto.capacidade());
        // Salvar a entidade editada no repositório
        commandPorta.salvar(tipoVeiculo);
    }
}
