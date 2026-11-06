package br.com.imbazzu.secretariaTransporte.veiculo.application.command.atribuirVeiculo;

import java.time.Duration;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.imbazzu.secretariaTransporte.agendamento.core.porta.out.AgendamentoCommandPorta;
import br.com.imbazzu.secretariaTransporte.agendamento.core.porta.out.AgendamentoQueryPorta;
import br.com.imbazzu.secretariaTransporte.agendamento.core.service.AgendamentoValidacao;
import br.com.imbazzu.secretariaTransporte.agendamento.core.service.CompatibilidadeVeiculoAgendamento;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.DadosInvalidosException;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.EntidadeNaoEncontradoException;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.OperacaoNaoPermitidaException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AgendamentoCommandAtribuirVeiculoHandler {

    private final AgendamentoCommandPorta comandos;
    private final AgendamentoQueryPorta consultas;

    @Transactional
    public void handler(AgendamentoCommandAtribuirVeiculo command) {
        var agendamento = AgendamentoValidacao.buscarAgendamento(consultas, command.idAgendamento());
        var veiculo = consultas.buscarVeiculo(command.idVeiculo())
                .orElseThrow(() -> new EntidadeNaoEncontradoException(
                        "Veículo aberto não encontrado: " + command.idVeiculo()));
        if (!agendamento.getData().equals(veiculo.data())) {
            throw new DadosInvalidosException("O agendamento e o veículo devem pertencer ao mesmo dia");
        }
        if (agendamento.getIdVeiculoAberto() != null) {
            throw new OperacaoNaoPermitidaException(
                    "O agendamento já está atribuído a um veículo físico");
        }
        var ocupantes = consultas.listarAgendamentos(agendamento.getData()).stream()
                .filter(item -> command.idVeiculo().equals(item.getIdVeiculoAberto()))
                .toList();
        if (!CompatibilidadeVeiculoAgendamento.capacidadeDisponivel(veiculo, ocupantes, agendamento)) {
            throw new OperacaoNaoPermitidaException(
                    "O veículo não possui assentos suficientes para este passageiro e seus acompanhantes");
        }
        var tolerancia = veiculo.calculaHorarioSaida()
                ? consultas.buscarToleranciaSeparacao(agendamento.getIdLista(), veiculo.idTipoVeiculo())
                        .orElse(Duration.ZERO)
                : Duration.ZERO;
        agendamento.atribuirVeiculo(command.idVeiculo(), veiculo.idTipoVeiculo(), tolerancia);
        comandos.salvar(agendamento);
    }
}
