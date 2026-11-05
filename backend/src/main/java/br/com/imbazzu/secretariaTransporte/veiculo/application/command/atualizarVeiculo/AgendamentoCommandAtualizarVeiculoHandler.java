package br.com.imbazzu.secretariaTransporte.veiculo.application.command.atualizarVeiculo;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.imbazzu.secretariaTransporte.agendamento.core.porta.out.AgendamentoCommandPorta;
import br.com.imbazzu.secretariaTransporte.agendamento.core.porta.out.AgendamentoQueryPorta;
import br.com.imbazzu.secretariaTransporte.agendamento.core.service.AgendamentoValidacao;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.DadosInvalidosException;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.EntidadeNaoEncontradoException;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.OperacaoNaoPermitidaException;
import br.com.imbazzu.secretariaTransporte.veiculo.core.domain.VeiculoAberto;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AgendamentoCommandAtualizarVeiculoHandler {

    private final AgendamentoCommandPorta comandos;
    private final AgendamentoQueryPorta consultas;

    @Transactional
    public VeiculoAberto handler(AgendamentoCommandAtualizarVeiculo command) {
        var atual = consultas.buscarVeiculo(command.idVeiculo())
                .orElseThrow(() -> new EntidadeNaoEncontradoException(
                        "Veículo aberto não encontrado: " + command.idVeiculo()));
        AgendamentoValidacao.exigirDiaAberto(consultas, atual.data());

        var identificacao = command.identificacao();
        if (identificacao != null) {
            identificacao = identificacao.trim();
            if (identificacao.isEmpty()) {
                identificacao = null;
            }
        }

        var horarioCalculado = VeiculoAberto.tipoCalculaHorarioSaida(atual.nomeTipoVeiculo());
        if (horarioCalculado && command.horarioSaida() != null) {
            throw new DadosInvalidosException("O horário de saída de veículos do tipo Carro é calculado");
        }
        if (!horarioCalculado && command.horarioSaida() == null) {
            throw new DadosInvalidosException("Informe o horário de saída para este tipo de veículo");
        }

        var atualizado = new VeiculoAberto(
                atual.id(),
                atual.data(),
                atual.idTipoVeiculo(),
                atual.nomeTipoVeiculo(),
                atual.capacidade(),
                identificacao,
                command.horarioSaida(),
                horarioCalculado);
        if (!horarioCalculado) {
            var passageiros = consultas.listarAgendamentos(atual.data()).stream()
                    .filter(agendamento -> atual.id().equals(agendamento.getIdVeiculoAberto()))
                    .toList();
            boolean todosCompativeis = passageiros.stream().allMatch(passageiro ->
                    !command.horarioSaida().isAfter(
                            br.com.imbazzu.secretariaTransporte.agendamento.core.service
                                    .CompatibilidadeVeiculoAgendamento.horarioSaidaNecessario(passageiro)));
            if (!todosCompativeis) {
                throw new OperacaoNaoPermitidaException(
                        "O horário de saída não atende aos horários de chegada dos passageiros atribuídos");
            }
        }
        return comandos.salvar(atualizado);
    }
}
