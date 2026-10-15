package br.com.imbazzu.secretariaTransporte.tipoVeiculo.application.command.deletar;

import org.springframework.stereotype.Service;

import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.porta.out.TipoVeiculoCommandPorta;
import lombok.RequiredArgsConstructor;

/**
 * TipoVeiculoCommandDeletarHandle
 */
@Service
@RequiredArgsConstructor
public class TipoVeiculoCommandDeletarHandle {

    private final TipoVeiculoCommandPorta porta;

    public void handler(TipoVeiculoCommandDeletar dto) {
        porta.deletar(dto.id());
    }
}
