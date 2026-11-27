package br.com.imbazzu.secretariaTransporte.agendamento.application.command.remover;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.imbazzu.secretariaTransporte.agendamento.core.porta.out.AgendamentoCommandPorta;
import br.com.imbazzu.secretariaTransporte.agendamento.core.porta.out.AgendamentoQueryPorta;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.EntidadeNaoEncontradoException;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.OperacaoNaoPermitidaException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AgendamentoCommandRemoverHandler {

    private final AgendamentoCommandPorta comandos;
    private final AgendamentoQueryPorta consultas;

    @Transactional
    public void handler(AgendamentoCommandRemover command) {
        var agendamento = consultas.buscarDadosRemocao(command.id())
                .orElseThrow(() -> new EntidadeNaoEncontradoException(
                        "Agendamento não encontrado: " + command.id()));
        if (!agendamento.cancelado()) {
            throw new OperacaoNaoPermitidaException("Cancele a viagem antes de excluir o agendamento");
        }
        if (agendamento.idVeiculoAberto() != null) {
            throw new OperacaoNaoPermitidaException(
                    "Remova o passageiro do veículo antes de excluir o agendamento");
        }
        comandos.deletar(command.id());
    }
}
