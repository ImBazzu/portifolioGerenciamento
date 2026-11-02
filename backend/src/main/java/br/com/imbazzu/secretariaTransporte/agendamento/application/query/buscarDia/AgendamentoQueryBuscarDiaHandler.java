package br.com.imbazzu.secretariaTransporte.agendamento.application.query.buscarDia;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.imbazzu.secretariaTransporte.agendamento.application.query.model.AgendaDia;
import br.com.imbazzu.secretariaTransporte.agendamento.application.query.model.AgendamentoAgenda;
import br.com.imbazzu.secretariaTransporte.agendamento.application.query.model.VeiculoAgenda;
import br.com.imbazzu.secretariaTransporte.agendamento.core.domain.Agendamento;
import br.com.imbazzu.secretariaTransporte.agendamento.core.domain.VeiculoAberto;
import br.com.imbazzu.secretariaTransporte.agendamento.core.porta.out.AgendamentoQueryPorta;
import br.com.imbazzu.secretariaTransporte.agendamento.core.service.AgendamentoValidacao;
import br.com.imbazzu.secretariaTransporte.agendamento.core.service.CompatibilidadeVeiculoAgendamento;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AgendamentoQueryBuscarDiaHandler {

    private final AgendamentoQueryPorta consultas;

    @Transactional(readOnly = true)
    public AgendaDia handler(AgendamentoQueryBuscarDia query) {
        AgendamentoValidacao.exigirDiaAberto(consultas, query.data());
        var agendamentos = consultas.listarAgendamentos(query.data());
        var veiculos = consultas.listarVeiculos(query.data());
        var compativeis = CompatibilidadeVeiculoAgendamento.listarCompativeis(agendamentos, veiculos);
        var passageirosPorVeiculo = agendamentos.stream()
                .filter(agendamento -> agendamento.getIdVeiculoAberto() != null)
                .collect(Collectors.groupingBy(Agendamento::getIdVeiculoAberto));
        var veiculosAgenda = veiculos.stream()
                .map(veiculo -> paraAgenda(veiculo,
                        passageirosPorVeiculo.getOrDefault(veiculo.id(), List.of())))
                .toList();
        var pendentes = agendamentos.stream()
                .filter(agendamento -> agendamento.getIdVeiculoAberto() == null)
                .sorted(Comparator.comparing(Agendamento::getHorarioChegada))
                .map(agendamento -> paraAgenda(
                        agendamento, compativeis.getOrDefault(agendamento.getId(), List.of())))
                .toList();
        return new AgendaDia(query.data(), veiculosAgenda, pendentes);
    }

    private static VeiculoAgenda paraAgenda(VeiculoAberto veiculo, List<Agendamento> passageiros) {
        var ocupantes = passageiros.stream()
                .map(agendamento -> paraAgenda(agendamento, List.of()))
                .toList();
        var assentosOcupados = passageiros.stream().mapToInt(Agendamento::quantidadePessoas).sum();
        var horarioSaida = CompatibilidadeVeiculoAgendamento.horarioSaidaDoVeiculo(veiculo, passageiros);
        return new VeiculoAgenda(
                veiculo.id(),
                veiculo.data(),
                veiculo.idTipoVeiculo(),
                veiculo.nomeTipoVeiculo(),
                veiculo.capacidade(),
                veiculo.identificacao(),
                horarioSaida,
                assentosOcupados,
                ocupantes);
    }

    private static AgendamentoAgenda paraAgenda(Agendamento agendamento, List<UUID> idsVeiculosCompativeis) {
        return new AgendamentoAgenda(
                agendamento.getId(),
                agendamento.getData(),
                agendamento.getIdPessoa(),
                agendamento.getIdLista(),
                agendamento.getIdRegraCadastro(),
                agendamento.getIdTipoVeiculo(),
                agendamento.getIdDestino(),
                agendamento.getHorarioChegada(),
                CompatibilidadeVeiculoAgendamento.horarioSaidaNecessario(agendamento),
                agendamento.isBuscarNaResidencia(),
                agendamento.getAcompanhantes(),
                agendamento.getIdVeiculoAberto(),
                idsVeiculosCompativeis);
    }
}
