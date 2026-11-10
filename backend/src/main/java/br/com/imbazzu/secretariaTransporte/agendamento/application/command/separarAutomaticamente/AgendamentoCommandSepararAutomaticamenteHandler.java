package br.com.imbazzu.secretariaTransporte.agendamento.application.command.separarAutomaticamente;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.imbazzu.secretariaTransporte.agendamento.application.AgendamentoCompatibilidadeResolver;
import br.com.imbazzu.secretariaTransporte.agendamento.application.command.model.ResultadoSeparacaoAutomatica;
import br.com.imbazzu.secretariaTransporte.agendamento.core.domain.Agendamento;
import br.com.imbazzu.secretariaTransporte.agendamento.core.model.TipoVeiculoAgendaDados;
import br.com.imbazzu.secretariaTransporte.agendamento.core.porta.out.AgendamentoCommandPorta;
import br.com.imbazzu.secretariaTransporte.agendamento.core.porta.out.AgendamentoQueryPorta;
import br.com.imbazzu.secretariaTransporte.agendamento.core.service.AgendamentoValidacao;
import br.com.imbazzu.secretariaTransporte.agendamento.core.service.CompatibilidadeVeiculoAgendamento;
import br.com.imbazzu.secretariaTransporte.veiculo.core.domain.VeiculoAberto;
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
        var tipoCarroPorId = new HashMap<UUID, Boolean>();
        for (var agendamento : todos) {
            var slot = CompatibilidadeVeiculoAgendamento.criarSlot(agendamento);
            slotsConhecidos.put(agendamento.getId(), slot);
            if (agendamento.getIdVeiculoAberto() == null
                    && tipoCarroPorId.computeIfAbsent(
                            agendamento.getIdTipoVeiculo(),
                            idTipo -> VeiculoAberto.tipoCalculaHorarioSaida(
                                    consultas.buscarTipoVeiculo(idTipo).nome()))) {
                pendentes.add(slot);
            }
        }
        pendentes.sort(Comparator.comparing(slot -> slot.agendamento().getHorarioChegada()));
        var veiculos = new ArrayList<>(consultas.listarVeiculos(command.data()));
        var ocupantesPorVeiculo = ocupantesPorVeiculo(todos);
        var toleranciasPorTipo = AgendamentoCompatibilidadeResolver.toleranciasPorTipo(consultas, todos);
        var tiposVeiculoPorId = new HashMap<UUID, TipoVeiculoAgendaDados>();
        var destinosPorCidadeId = consultas.buscarDestinosPorCidadeId(command.data());

        int alocados = 0;
        for (var slot : pendentes) {
            var tolerancias = toleranciasPorTipo.getOrDefault(slot.agendamento().getId(), Map.of());
            if (tolerancias.isEmpty()) {
                motivosPendencia.put(slot.agendamento().getId(),
                        "A regra de cadastro não permite tipos de veículo compatíveis com esta pessoa");
                continue;
            }
            var veiculoEscolhido = veiculos.stream()
                    .filter(veiculo -> VeiculoAberto.tipoCalculaHorarioSaida(veiculo.nomeTipoVeiculo()))
                    .filter(veiculo -> tolerancias.containsKey(veiculo.idTipoVeiculo()))
                    .filter(v->CompatibilidadeVeiculoAgendamento.compativelCidade(
                            destinosPorCidadeId,
                            ocupantesPorVeiculo.getOrDefault(v.id(), List.of()),
                            slot.agendamento()))
                    .filter(veiculo -> CompatibilidadeVeiculoAgendamento.compativel(
                            veiculo,
                            ocupantesPorVeiculo.getOrDefault(veiculo.id(), List.of()),
                            CompatibilidadeVeiculoAgendamento.criarSlot(
                                    slot.agendamento(), tolerancias.get(veiculo.idTipoVeiculo())),
                            slotsConhecidos))
                    .min(Comparator.comparingInt(veiculo -> CompatibilidadeVeiculoAgendamento.capacidadeRestante(
                            veiculo,
                            ocupantesPorVeiculo.getOrDefault(veiculo.id(), List.of()),
                            slot.agendamento())))
                    .orElse(null);
            if (veiculoEscolhido == null) {
                var tiposCarroPermitidos = tolerancias.keySet().stream()
                        .map(idTipo -> tiposVeiculoPorId.computeIfAbsent(idTipo, consultas::buscarTipoVeiculo))
                        .filter(tipo -> VeiculoAberto.tipoCalculaHorarioSaida(tipo.nome()))
                        .toList();
                var tipoCarro = tiposCarroPermitidos.stream()
                        .filter(TipoVeiculoAgendaDados::ativo)
                        .filter(tipo -> tipo.capacidade() >= slot.agendamento().quantidadePessoas())
                        .min(Comparator.comparingInt(
                                tipo -> tipo.capacidade() - slot.agendamento().quantidadePessoas()))
                        .orElse(null);
                if (tipoCarro == null) {
                    motivosPendencia.put(slot.agendamento().getId(),
                            motivoSemCarroCompativel(tiposCarroPermitidos, slot.agendamento()));
                    continue;
                }

                veiculoEscolhido = comandos.salvar(new VeiculoAberto(
                        UUID.randomUUID(),
                        command.data(),
                        tipoCarro.id(),
                        tipoCarro.nome(),
                        tipoCarro.capacidade(),
                        null,
                        null,
                        true));
                veiculos.add(veiculoEscolhido);
                ocupantesPorVeiculo.put(veiculoEscolhido.id(), new ArrayList<>());
            }

            var tolerancia = tolerancias.get(veiculoEscolhido.idTipoVeiculo());
            slot.agendamento().atribuirVeiculo(
                    veiculoEscolhido.id(), veiculoEscolhido.idTipoVeiculo(), tolerancia);
            comandos.salvar(slot.agendamento());
            slotsConhecidos.put(
                    slot.agendamento().getId(), CompatibilidadeVeiculoAgendamento.criarSlot(slot.agendamento()));
            ocupantesPorVeiculo.computeIfAbsent(veiculoEscolhido.id(), ignored -> new ArrayList<>())
                    .add(slot.agendamento());
            alocados++;
        }

        var aindaPendentes = consultas.listarAgendamentos(command.data()).stream()
                .filter(agendamento -> agendamento.getIdVeiculoAberto() == null)
                .filter(agendamento -> tipoCarroPorId.computeIfAbsent(
                        agendamento.getIdTipoVeiculo(),
                        idTipo -> VeiculoAberto.tipoCalculaHorarioSaida(
                                consultas.buscarTipoVeiculo(idTipo).nome())))
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

    private static String motivoSemCarroCompativel(
            List<TipoVeiculoAgendaDados> tiposCarroPermitidos,
            Agendamento agendamento) {
        if (tiposCarroPermitidos.isEmpty()) {
            return "A regra de cadastro não permite tipos de veículo Carro";
        }
        if (tiposCarroPermitidos.stream().noneMatch(tipo ->
                tipo.ativo() && tipo.capacidade() >= agendamento.quantidadePessoas())) {
            return "Não há tipo Carro ativo permitido pela regra com capacidade suficiente";
        }
        return "Não foi possível abrir um Carro compatível com as regras de separação";
    }

}
