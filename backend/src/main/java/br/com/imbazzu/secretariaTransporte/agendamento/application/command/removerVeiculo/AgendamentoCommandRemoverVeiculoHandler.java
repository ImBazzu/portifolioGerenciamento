package br.com.imbazzu.secretariaTransporte.agendamento.application.command.removerVeiculo;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.imbazzu.secretariaTransporte.agendamento.core.porta.out.AgendamentoCommandPorta;
import br.com.imbazzu.secretariaTransporte.agendamento.core.porta.out.AgendamentoQueryPorta;
import br.com.imbazzu.secretariaTransporte.agendamento.core.service.AgendamentoValidacao;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.OperacaoNaoPermitidaException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AgendamentoCommandRemoverVeiculoHandler {

    private final AgendamentoCommandPorta comandos;
    private final AgendamentoQueryPorta consultas;

    @Transactional
    public void handler(AgendamentoCommandRemoverVeiculo command) {
        var agendamento = AgendamentoValidacao.buscarAgendamento(consultas, command.idAgendamento());
        if (agendamento.getIdVeiculoAberto() == null) {
            throw new OperacaoNaoPermitidaException("O agendamento não está atribuído a um veículo");
        }
        agendamento.removerVeiculo();
        comandos.salvar(agendamento);
    }
}
