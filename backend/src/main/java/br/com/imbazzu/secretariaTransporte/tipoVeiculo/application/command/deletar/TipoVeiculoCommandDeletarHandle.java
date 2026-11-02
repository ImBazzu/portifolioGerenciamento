package br.com.imbazzu.secretariaTransporte.tipoVeiculo.application.command.deletar;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.OperacaoNaoPermitidaException;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.porta.out.TipoVeiculoCommandPorta;
import lombok.RequiredArgsConstructor;

/**
 * TipoVeiculoCommandDeletarHandle
 */
@Service
@RequiredArgsConstructor
public class TipoVeiculoCommandDeletarHandle {

    private final TipoVeiculoCommandPorta porta;

    @Transactional
    public void handler(TipoVeiculoCommandDeletar dto) {
        var veiculo = porta.buscarPorId(dto.id());
        if (veiculo.getDataArquivado() == null) {
            throw new OperacaoNaoPermitidaException("O tipo de veículo precisa estar arquivado para ser excluído");
        }
        porta.deletar(dto.id());
    }
}
