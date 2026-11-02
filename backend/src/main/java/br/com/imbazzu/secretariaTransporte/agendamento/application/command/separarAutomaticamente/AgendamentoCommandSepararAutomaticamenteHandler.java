package br.com.imbazzu.secretariaTransporte.agendamento.application.command.separarAutomaticamente;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.imbazzu.secretariaTransporte.agendamento.application.command.model.ResultadoSeparacaoAutomatica;
import br.com.imbazzu.secretariaTransporte.agendamento.core.domain.Agendamento;
import br.com.imbazzu.secretariaTransporte.agendamento.core.domain.VeiculoAberto;
import br.com.imbazzu.secretariaTransporte.agendamento.core.porta.out.AgendamentoCommandPorta;
import br.com.imbazzu.secretariaTransporte.agendamento.core.porta.out.AgendamentoQueryPorta;
import br.com.imbazzu.secretariaTransporte.agendamento.core.service.AgendamentoValidacao;
import br.com.imbazzu.secretariaTransporte.agendamento.core.service.CompatibilidadeVeiculoAgendamento;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AgendamentoCommandSepararAutomaticamenteHandler {

    private final AgendamentoCommandPorta comandos;
    private final AgendamentoQueryPorta consultas;

    @Transactional
    public ResultadoSeparacaoAutomatica handler(AgendamentoCommandSepararAutomaticamente command) {
        AgendamentoValidacao.exigirDiaAberto(consultas, command.data());
        var todos = consultas.listarAgendamentos(command.data());
        var motivosPendencia = new HashMap<UUID, String>();
        var pendentes = new ArrayList<CompatibilidadeVeiculoAgendamento.Slot>();
        var slotsConhecidos = new HashMap<UUID, CompatibilidadeVeiculoAgendamento.Slot>();
        for (var agendamento : todos) {
            var slot = CompatibilidadeVeiculoAgendamento.criarSlot(agendamento);
            slotsConhecidos.put(agendamento.getId(), slot);
            if (agendamento.getIdVeiculoAberto() == null) {
                pendentes.add(slot);
            }
        }
        pendentes.sort(Comparator.comparing(slot -> slot.agendamento().getHorarioChegada()));
        var veiculos = consultas.listarVeiculos(command.data());
        var ocupantesPorVeiculo = ocupantesPorVeiculo(todos);

        int alocados = 0;
        for (var slot : pendentes) {
            var veiculosCompativeis = veiculos.stream()
                    .filter(veiculo -> veiculo.idTipoVeiculo().equals(slot.agendamento().getIdTipoVeiculo()))
                    .filter(veiculo -> CompatibilidadeVeiculoAgendamento.compativel(
                            veiculo,
                            ocupantesPorVeiculo.getOrDefault(veiculo.id(), List.of()),
                            slot,
                            slotsConhecidos))
                    .sorted(Comparator.comparingInt(veiculo -> CompatibilidadeVeiculoAgendamento.capacidadeRestante(
                            veiculo,
                            ocupantesPorVeiculo.getOrDefault(veiculo.id(), List.of()),
                            slot.agendamento())))
                    .toList();
            if (veiculosCompativeis.isEmpty()) {
                var doTipo = veiculos.stream()
                        .filter(veiculo -> veiculo.idTipoVeiculo().equals(slot.agendamento().getIdTipoVeiculo()))
                        .toList();
                motivosPendencia.put(slot.agendamento().getId(), motivoPendencia(
                        doTipo, ocupantesPorVeiculo, slot.agendamento()));
                continue;
            }

            var escolhido = veiculosCompativeis.getFirst();
            slot.agendamento().atribuirVeiculo(escolhido.id());
            comandos.salvar(slot.agendamento());
            ocupantesPorVeiculo.computeIfAbsent(escolhido.id(), ignored -> new ArrayList<>())
                    .add(slot.agendamento());
            alocados++;
        }

        var aindaPendentes = consultas.listarAgendamentos(command.data()).stream()
                .filter(agendamento -> agendamento.getIdVeiculoAberto() == null)
                .sorted(Comparator.comparing(Agendamento::getHorarioChegada))
                .map(agendamento -> new ResultadoSeparacaoAutomatica.PassageiroPendente(
                        agendamento.getId(),
                        motivosPendencia.getOrDefault(agendamento.getId(),
                                "Nenhum veículo compatível com a capacidade e o horário foi encontrado")))
                .toList();
        return new ResultadoSeparacaoAutomatica(alocados, aindaPendentes);
    }

    private static HashMap<UUID, List<Agendamento>> ocupantesPorVeiculo(List<Agendamento> agendamentos) {
        var resultado = new HashMap<UUID, List<Agendamento>>();
        for (var agendamento : agendamentos) {
            if (agendamento.getIdVeiculoAberto() != null) {
                resultado.computeIfAbsent(agendamento.getIdVeiculoAberto(), ignored -> new ArrayList<>())
                        .add(agendamento);
            }
        }
        return resultado;
    }

    private static String motivoPendencia(
            List<VeiculoAberto> veiculos,
            HashMap<UUID, List<Agendamento>> ocupantesPorVeiculo,
            Agendamento agendamento) {
        if (veiculos.isEmpty()) {
            return "Nenhum veículo físico deste tipo foi aberto para o dia";
        }
        if (veiculos.stream().noneMatch(veiculo -> CompatibilidadeVeiculoAgendamento.capacidadeDisponivel(
                veiculo, ocupantesPorVeiculo.getOrDefault(veiculo.id(), List.of()), agendamento))) {
            return "Não há assentos disponíveis nos veículos deste tipo";
        }
        return "Horário de saída incompatível com a tolerância das regras";
    }
}
