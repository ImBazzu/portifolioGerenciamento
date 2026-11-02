package br.com.imbazzu.secretariaTransporte.agendamento.core.service;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import br.com.imbazzu.secretariaTransporte.agendamento.core.domain.Agendamento;
import br.com.imbazzu.secretariaTransporte.agendamento.core.domain.VeiculoAberto;

public final class CompatibilidadeVeiculoAgendamento {

    private CompatibilidadeVeiculoAgendamento() {
    }

    public static Map<UUID, List<UUID>> listarCompativeis(
            List<Agendamento> agendamentos, List<VeiculoAberto> veiculos) {
        var slotsConhecidos = new HashMap<UUID, Slot>();
        var ocupantesPorVeiculo = new HashMap<UUID, List<Agendamento>>();
        for (var agendamento : agendamentos) {
            slotsConhecidos.put(agendamento.getId(), criarSlot(agendamento));
            if (agendamento.getIdVeiculoAberto() != null) {
                ocupantesPorVeiculo.computeIfAbsent(
                        agendamento.getIdVeiculoAberto(), ignored -> new ArrayList<>()).add(agendamento);
            }
        }

        var resultado = new HashMap<UUID, List<UUID>>();
        for (var agendamento : agendamentos) {
            if (agendamento.getIdVeiculoAberto() != null) {
                continue;
            }
            var slot = slotsConhecidos.get(agendamento.getId());
            var idsCompativeis = veiculos.stream()
                    .filter(veiculo -> veiculo.idTipoVeiculo().equals(agendamento.getIdTipoVeiculo()))
                    .filter(veiculo -> compativel(
                            veiculo, ocupantesPorVeiculo.getOrDefault(veiculo.id(), List.of()),
                            slot, slotsConhecidos))
                    .map(VeiculoAberto::id)
                    .toList();
            resultado.put(agendamento.getId(), idsCompativeis);
        }
        return resultado;
    }

    public static boolean compativel(
            VeiculoAberto veiculo,
            List<Agendamento> ocupantes,
            Slot novo,
            Map<UUID, Slot> slotsConhecidos) {
        return capacidadeDisponivel(veiculo, ocupantes, novo.agendamento())
                && compativelComHorario(veiculo, ocupantes, novo, slotsConhecidos);
    }

    public static boolean capacidadeDisponivel(
            VeiculoAberto veiculo, List<Agendamento> ocupantes, Agendamento novo) {
        long lugaresOcupados = ocupantes.stream().mapToLong(Agendamento::quantidadePessoas).sum();
        return lugaresOcupados + novo.quantidadePessoas() <= veiculo.capacidade();
    }

    public static int capacidadeRestante(
            VeiculoAberto veiculo, List<Agendamento> ocupantes, Agendamento novo) {
        long lugaresOcupados = ocupantes.stream().mapToLong(Agendamento::quantidadePessoas).sum();
        return (int) (veiculo.capacidade() - lugaresOcupados - novo.quantidadePessoas());
    }

    public static Slot criarSlot(Agendamento agendamento) {
        return new Slot(agendamento, agendamento.getHorarioChegada().minus(agendamento.getTempoViagem()));
    }

    public static LocalTime horarioSaidaNecessario(Agendamento agendamento) {
        return agendamento.getHorarioChegada().minus(agendamento.getTempoViagem());
    }

    public static LocalTime horarioSaidaDoVeiculo(VeiculoAberto veiculo, List<Agendamento> agendamentos) {
        if (!veiculo.calculaHorarioSaida()) {
            return veiculo.horarioSaida();
        }
        return agendamentos.stream()
                .filter(agendamento -> veiculo.id().equals(agendamento.getIdVeiculoAberto()))
                .map(CompatibilidadeVeiculoAgendamento::horarioSaidaNecessario)
                .min(LocalTime::compareTo)
                .orElse(null);
    }

    private static boolean compativelComHorario(
            VeiculoAberto veiculo,
            List<Agendamento> ocupantes,
            Slot novo,
            Map<UUID, Slot> slotsConhecidos) {
        var novosDados = novo.agendamento();
        var novaSaidaNecessaria = novo.horarioSaidaNecessario();
        if (!veiculo.calculaHorarioSaida()
                && (veiculo.horarioSaida().isAfter(novaSaidaNecessaria)
                        || Duration.between(veiculo.horarioSaida(), novaSaidaNecessaria)
                                .compareTo(novosDados.getToleranciaSeparacao()) > 0)) {
            return false;
        }
        for (var ocupante : ocupantes) {
            var slotOcupante = slotsConhecidos.get(ocupante.getId());
            if (slotOcupante == null) {
                return false;
            }
            var diferenca = Duration.between(
                    slotOcupante.horarioSaidaNecessario(), novaSaidaNecessaria).abs();
            var toleranciaOcupante = slotOcupante.agendamento().getToleranciaSeparacao();
            var toleranciaRestritiva = toleranciaOcupante.compareTo(novosDados.getToleranciaSeparacao()) <= 0
                    ? toleranciaOcupante
                    : novosDados.getToleranciaSeparacao();
            if (diferenca.compareTo(toleranciaRestritiva) > 0) {
                return false;
            }
        }
        return true;
    }

    public record Slot(Agendamento agendamento, LocalTime horarioSaidaNecessario) {
    }
}
