package br.com.imbazzu.secretariaTransporte.tipoVeiculo.application.command.desarquivar;

import org.springframework.stereotype.Service;

import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.porta.out.TipoVeiculoCommandPorta;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TipoVeiculoCommandDesarquivarHandler {

    private final TipoVeiculoCommandPorta commandPorta;

    public void handler(TipoVeiculoCommandDesarquivar dto) {
        var veiculo = commandPorta.buscarPorId(dto.id());
        veiculo.desarquivar();
        commandPorta.salvar(veiculo);
    }
}
