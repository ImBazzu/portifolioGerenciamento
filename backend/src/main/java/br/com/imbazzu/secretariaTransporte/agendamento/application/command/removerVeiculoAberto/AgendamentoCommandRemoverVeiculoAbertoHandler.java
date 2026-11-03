package br.com.imbazzu.secretariaTransporte.agendamento.application.command.removerVeiculoAberto;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.imbazzu.secretariaTransporte.agendamento.core.porta.out.AgendamentoCommandPorta;
import br.com.imbazzu.secretariaTransporte.agendamento.core.porta.out.AgendamentoQueryPorta;
import br.com.imbazzu.secretariaTransporte.agendamento.core.service.AgendamentoValidacao;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.EntidadeNaoEncontradoException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AgendamentoCommandRemoverVeiculoAbertoHandler {

    private final AgendamentoCommandPorta comandos;
    private final AgendamentoQueryPorta consultas;

    @Transactional
    public void handler(AgendamentoCommandRemoverVeiculoAberto command) {
        var veiculo = consultas.buscarVeiculo(command.idVeiculo())
                .orElseThrow(() -> new EntidadeNaoEncontradoException(
                        "Veículo aberto não encontrado: " + command.idVeiculo()));
        AgendamentoValidacao.exigirDiaAberto(consultas, veiculo.data());

        consultas.listarAgendamentos(veiculo.data()).stream()
                .filter(agendamento -> veiculo.id().equals(agendamento.getIdVeiculoAberto()))
                .forEach(agendamento -> {
                    agendamento.removerVeiculo();
                    comandos.salvar(agendamento);
                });
        comandos.deletarVeiculo(veiculo.id());
    }
}
