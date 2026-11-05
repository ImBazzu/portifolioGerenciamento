package br.com.imbazzu.secretariaTransporte.tipoVeiculo.application.command.desarquivar;

import org.springframework.stereotype.Service;

import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.porta.out.TipoVeiculoCommandPorta;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.service.TipoVeiculoServiceBuscarEntidade;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TipoVeiculoCommandDesarquivarHandler {

    private final TipoVeiculoCommandPorta commandPorta;

    private final TipoVeiculoServiceBuscarEntidade buscarEntidade;

    public void handler(TipoVeiculoCommandDesarquivar dto) {
        // Buscar a entidade pelo ID fornecido no DTO
        var veiculo = buscarEntidade.executar(dto.id());
        // Desarquivar a entidade
        veiculo.desarquivar();
        // Salvar a entidade desarquivada
        commandPorta.salvar(veiculo);
    }
}
