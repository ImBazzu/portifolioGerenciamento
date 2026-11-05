package br.com.imbazzu.secretariaTransporte.tipoVeiculo.application.command.arquivar;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.porta.out.TipoVeiculoCommandPorta;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.service.TipoVeiculoServiceBuscarEntidade;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TipoVeiculoCommandArquivarHandler {

    private final TipoVeiculoCommandPorta command;

    private final TipoVeiculoServiceBuscarEntidade buscarEntidade;

    @Transactional
    public void handler(TipoVeiculoCommandArquivar dto) {
        // Buscar a entidade pelo ID fornecido no DTO
        var tipoVeiculo = buscarEntidade.executar(dto.idTipoVeiculo());
        // Arquivar a entidade
        tipoVeiculo.arquivar();
        // Salvar a entidade arquivada
        command.salvar(tipoVeiculo);
    }
}
