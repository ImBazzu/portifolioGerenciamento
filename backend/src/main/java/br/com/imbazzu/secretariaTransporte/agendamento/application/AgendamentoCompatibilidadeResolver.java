package br.com.imbazzu.secretariaTransporte.agendamento.application;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import br.com.imbazzu.secretariaTransporte.agendamento.core.domain.Agendamento;
import br.com.imbazzu.secretariaTransporte.agendamento.core.model.DestinoAgendaDados;
import br.com.imbazzu.secretariaTransporte.agendamento.core.model.PessoaAgendaDados;
import br.com.imbazzu.secretariaTransporte.agendamento.core.model.RegraCadastroAgendaDados;
import br.com.imbazzu.secretariaTransporte.agendamento.core.porta.out.AgendamentoQueryPorta;
import br.com.imbazzu.secretariaTransporte.agendamento.core.service.RegraCadastroAgendamento;
import br.com.imbazzu.secretariaTransporte.veiculo.core.domain.VeiculoAberto;

public final class AgendamentoCompatibilidadeResolver {

    private AgendamentoCompatibilidadeResolver() {
    }

    public static Map<UUID, Map<UUID, Duration>> toleranciasPorTipo(
            AgendamentoQueryPorta consultas, List<Agendamento> agendamentos) {
        var regrasPorLista = new HashMap<UUID, List<RegraCadastroAgendaDados>>();
        var pessoasPorId = new HashMap<UUID, PessoaAgendaDados>();
        var destinosPorId = new HashMap<UUID, DestinoAgendaDados>();
        var toleranciasPorLista = new HashMap<UUID, Map<UUID, Optional<Duration>>>();
        var resultado = new HashMap<UUID, Map<UUID, Duration>>();

        for (var agendamento : agendamentos) {
            var regras = regrasPorLista.computeIfAbsent(
                    agendamento.getIdLista(),
                    consultas::listarRegrasCadastro);
            var regra = regras.stream()
                    .filter(item -> item.id().equals(agendamento.getIdRegraCadastro()))
                    .findFirst()
                    .orElse(null);
            if (regra == null) {
                resultado.put(agendamento.getId(),
                        Map.of(agendamento.getIdTipoVeiculo(), agendamento.getToleranciaSeparacao()));
                continue;
            }

            var pessoa = pessoasPorId.computeIfAbsent(
                    agendamento.getIdPessoa(), consultas::buscarPessoa);
            var destino = destinosPorId.computeIfAbsent(
                    agendamento.getIdDestino(), consultas::buscarDestino);
            if (regra.obrigacaoAnexo()
                    || !RegraCadastroAgendamento.aplica(
                            regra, pessoa, destino, agendamento.getHorarioChegada())) {
                resultado.put(agendamento.getId(), Map.of());
                continue;
            }

            var tolerancias = new HashMap<UUID, Duration>();
            for (var idTipo : regra.idsVeiculos()) {
                var tipo = consultas.buscarTipoVeiculo(idTipo);
                if (!VeiculoAberto.tipoCalculaHorarioSaida(tipo.nome())) {
                    tolerancias.put(idTipo, Duration.ZERO);
                    continue;
                }
                var tolerancia = toleranciasPorLista
                        .computeIfAbsent(agendamento.getIdLista(), ignored -> new HashMap<>())
                        .computeIfAbsent(idTipo, ignored -> consultas.buscarToleranciaSeparacao(
                                agendamento.getIdLista(), idTipo));
                tolerancia.ifPresent(valor -> tolerancias.put(idTipo, valor));
            }
            resultado.put(agendamento.getId(), Map.copyOf(tolerancias));
        }

        return Map.copyOf(resultado);
    }
}
