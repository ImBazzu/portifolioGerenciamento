package br.com.imbazzu.secretariaTransporte.veiculo.application.command.confirmarSaida;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.imbazzu.secretariaTransporte.agendamento.core.porta.out.AgendamentoCommandPorta;
import br.com.imbazzu.secretariaTransporte.agendamento.core.porta.out.AgendamentoQueryPorta;
import br.com.imbazzu.secretariaTransporte.agendamento.core.service.CompatibilidadeVeiculoAgendamento;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.EntidadeNaoEncontradoException;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.OperacaoNaoPermitidaException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AgendamentoCommandConfirmarSaidaHandler {

    private final AgendamentoCommandPorta comandos;
    private final AgendamentoQueryPorta consultas;

    @Transactional
    public void handler(AgendamentoCommandConfirmarSaida command) {
        var veiculo = consultas.buscarVeiculo(command.idVeiculo())
                .orElseThrow(() -> new EntidadeNaoEncontradoException(
                        "Veículo da agenda não encontrado: " + command.idVeiculo()));

        if (veiculo.saidaConfirmadaEm() != null) {
            throw new OperacaoNaoPermitidaException("A saída deste veículo já foi confirmada.");
        }

        var passageiros = consultas.listarAgendamentos(veiculo.data()).stream()
                .filter(passageiro -> command.idVeiculo().equals(passageiro.getIdVeiculoAberto()))
                .toList();
        if (CompatibilidadeVeiculoAgendamento.horarioSaidaDoVeiculo(veiculo, passageiros) == null) {
            throw new OperacaoNaoPermitidaException(
                    "Defina o horário de saída do veículo antes de confirmar.");
        }

        UUID autorId = command.autorId();
        if (!comandos.confirmarSaidaVeiculo(command.idVeiculo(), LocalDateTime.now(), autorId)) {
            throw new OperacaoNaoPermitidaException("A saída deste veículo já foi confirmada.");
        }

    }

    @Transactional
    public void desfazer(UUID idVeiculo) {
        consultas.buscarVeiculo(idVeiculo)
                .orElseThrow(() -> new EntidadeNaoEncontradoException(
                        "Veículo da agenda não encontrado: " + idVeiculo));
        if (!comandos.desfazerConfirmacaoSaidaVeiculo(idVeiculo)) {
            throw new OperacaoNaoPermitidaException("A saída deste veículo não está confirmada.");
        }
    }
}
