package br.com.imbazzu.secretariaTransporte.tipoVeiculo.application.command.arquivar;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.porta.out.TipoVeiculoCommandPorta;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TipoVeiculoCommandArquivarHandler {

    private final TipoVeiculoCommandPorta command;

    @Transactional
    public void handler(TipoVeiculoCommandArquivar dto) {
        var veiculo = command.buscarPorId(dto.idVeiculo());
        veiculo.arquivar();
        command.salvar(veiculo);
    }
}
