package br.com.imbazzu.secretariaTransporte.agendamento.application.command.remover;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.imbazzu.secretariaTransporte.agendamento.core.porta.out.AgendamentoCommandPorta;
import br.com.imbazzu.secretariaTransporte.agendamento.core.porta.out.AgendamentoQueryPorta;
import br.com.imbazzu.secretariaTransporte.agendamento.core.service.AgendamentoValidacao;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.OperacaoNaoPermitidaException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AgendamentoCommandRemoverHandler {

    private final AgendamentoCommandPorta comandos;
    private final AgendamentoQueryPorta consultas;

    @Transactional
    public void handler(AgendamentoCommandRemover command) {
        var agendamento = AgendamentoValidacao.buscarAgendamento(consultas, command.id());
        if (agendamento.getIdVeiculoAberto() != null) {
            throw new OperacaoNaoPermitidaException(
                    "Remova o passageiro do veículo antes de excluir o agendamento");
        }
        comandos.deletar(command.id());
    }
}
