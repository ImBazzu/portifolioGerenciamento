package br.com.imbazzu.secretariaTransporte.agendamento.application.command.atribuirVeiculo;

import java.util.HashMap;
import java.util.List;

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
        if (!agendamento.getIdTipoVeiculo().equals(veiculo.idTipoVeiculo())) {
            throw new DadosInvalidosException(
                    "O tipo do veículo não corresponde ao tipo necessário no agendamento");
        }
        if (agendamento.getIdVeiculoAberto() != null) {
            throw new OperacaoNaoPermitidaException(
                    "O agendamento já está atribuído a um veículo físico");
        }

        var ocupantes = consultas.listarAgendamentos(agendamento.getData()).stream()
                .filter(item -> command.idVeiculo().equals(item.getIdVeiculoAberto()))
                .toList();
        var slotsConhecidos = new HashMap<java.util.UUID, CompatibilidadeVeiculoAgendamento.Slot>();
        slotsConhecidos.put(agendamento.getId(), CompatibilidadeVeiculoAgendamento.criarSlot(agendamento));
        ocupantes.forEach(item -> slotsConhecidos.put(
                item.getId(), CompatibilidadeVeiculoAgendamento.criarSlot(item)));
        if (!CompatibilidadeVeiculoAgendamento.compativel(
                veiculo,
                ocupantes,
                slotsConhecidos.get(agendamento.getId()),
                slotsConhecidos)) {
            throw new OperacaoNaoPermitidaException(
                    "O veículo não atende à capacidade ou aos horários e tolerâncias deste passageiro");
        }
        agendamento.atribuirVeiculo(command.idVeiculo());
        comandos.salvar(agendamento);
    }
}
