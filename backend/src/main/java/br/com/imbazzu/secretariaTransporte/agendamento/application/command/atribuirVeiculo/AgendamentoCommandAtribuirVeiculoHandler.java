package br.com.imbazzu.secretariaTransporte.agendamento.application.command.atribuirVeiculo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.imbazzu.secretariaTransporte.agendamento.application.AgendamentoCompatibilidadeResolver;
import br.com.imbazzu.secretariaTransporte.agendamento.core.domain.VeiculoAberto;
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
        var tipoPassageiro = consultas.buscarTipoVeiculo(agendamento.getIdTipoVeiculo());
        var tipoVeiculo = consultas.buscarTipoVeiculo(veiculo.idTipoVeiculo());
        boolean passageiroVaiDeCarro = VeiculoAberto.tipoCalculaHorarioSaida(tipoPassageiro.nome());
        boolean veiculoEhCarro = VeiculoAberto.tipoCalculaHorarioSaida(tipoVeiculo.nome());
        if (passageiroVaiDeCarro != veiculoEhCarro) {
            throw new DadosInvalidosException(
                    "Passageiros de carro só podem ser atribuídos a carros; os demais usam ônibus ou vans");
        }
        if (agendamento.getIdVeiculoAberto() != null) {
            throw new OperacaoNaoPermitidaException(
                    "O agendamento já está atribuído a um veículo físico");
        }
        var toleranciasPorTipo = AgendamentoCompatibilidadeResolver.toleranciasPorTipo(
                consultas, List.of(agendamento)).getOrDefault(agendamento.getId(), Map.of());
        var tolerancia = toleranciasPorTipo.get(veiculo.idTipoVeiculo());
        if (tolerancia == null) {
            throw new DadosInvalidosException(
                    "O tipo do veículo não é permitido pela regra de cadastro deste passageiro");
        }
        var ocupantes = consultas.listarAgendamentos(agendamento.getData()).stream()
                .filter(item -> command.idVeiculo().equals(item.getIdVeiculoAberto()))
                .toList();
        var slotsConhecidos = new HashMap<java.util.UUID, CompatibilidadeVeiculoAgendamento.Slot>();
        var novoSlot = CompatibilidadeVeiculoAgendamento.criarSlot(agendamento, tolerancia);
        slotsConhecidos.put(agendamento.getId(), novoSlot);
        ocupantes.forEach(item -> slotsConhecidos.put(
                item.getId(), CompatibilidadeVeiculoAgendamento.criarSlot(item)));
        if (!CompatibilidadeVeiculoAgendamento.compativel(
                veiculo,
                ocupantes,
                novoSlot,
                slotsConhecidos)) {
            throw new OperacaoNaoPermitidaException(
                    "O veículo não atende à capacidade ou aos horários e tolerâncias deste passageiro");
        }
        agendamento.atribuirVeiculo(command.idVeiculo(), veiculo.idTipoVeiculo(), tolerancia);
        comandos.salvar(agendamento);
    }
}
