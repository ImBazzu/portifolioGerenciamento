package br.com.imbazzu.secretariaTransporte.tipoVeiculo.application.command.deletar;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.OperacaoNaoPermitidaException;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.porta.out.TipoVeiculoCommandPorta;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.service.TipoVeiculoServiceBuscarEntidade;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.service.TipoVeiculoServiceValidarExclusao;
import lombok.RequiredArgsConstructor;

/**
 * TipoVeiculoCommandDeletarHandle
 */
@Service
@RequiredArgsConstructor
public class TipoVeiculoCommandDeletarHandle {

    private final TipoVeiculoCommandPorta porta;
    
    private final TipoVeiculoServiceBuscarEntidade serviceBuscar;
    private final TipoVeiculoServiceValidarExclusao serviceValidar;

    @Transactional
    public void handler(TipoVeiculoCommandDeletar dto) {
        //Busca a entidade no banco de dados, caso não exista lança exceção
        var veiculo = serviceBuscar.executar(dto.id());
        //Valida se o tipo de veículo pode ser excluído, caso não possa lança exceção
        serviceValidar.executar(veiculo);
        //Deleta o tipo de veículo do banco de dados
        porta.deletar(dto.id());
    }
}
