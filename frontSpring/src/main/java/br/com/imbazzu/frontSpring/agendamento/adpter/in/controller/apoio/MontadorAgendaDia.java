package br.com.imbazzu.frontSpring.agendamento.adpter.in.controller.apoio;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.ui.Model;

import br.com.imbazzu.frontSpring.agendamento.adpter.in.controller.view.OpcoesAgendamento;
import br.com.imbazzu.frontSpring.agendamento.adpter.in.controller.view.VeiculoAtribuidoView;
import br.com.imbazzu.frontSpring.agendamento.core.dto.AgendaDiaResponseDto;
import br.com.imbazzu.frontSpring.agendamento.core.dto.AgendamentoResponseDto;
import br.com.imbazzu.frontSpring.agendamento.core.dto.VeiculoAbertoResponseDto;
import br.com.imbazzu.frontSpring.veiculo.core.TipoVeiculoUtil;

/** Prepara os atributos de model derivados da agenda do dia. */
public class MontadorAgendaDia {

    public void dividirPorTipo(Model model, AgendaDiaResponseDto agenda, OpcoesAgendamento opcoes) {
        var nomesPorTipo = opcoes.nomesTipoVeiculo();

        model.addAttribute("nomesTipoVeiculo", nomesPorTipo);
        var veiculosNaoCarro = agenda.veiculos().stream()
                .filter(v -> !TipoVeiculoUtil.ehCarro(v.nomeTipoVeiculo()))
                .toList();
        var veiculosCarro = agenda.veiculos().stream()
                .filter(v -> TipoVeiculoUtil.ehCarro(v.nomeTipoVeiculo()))
                .toList();
        model.addAttribute("veiculosNaoCarro", veiculosNaoCarro);
        model.addAttribute("veiculosCarro", veiculosCarro);
        model.addAttribute("veiculosNaoCarroAntes0530", veiculosNaoCarro.stream()
                .filter(v -> "ANTES_0530".equals(v.faixaHorarioSaida())).toList());
        model.addAttribute("veiculosCarroAntes0530", veiculosCarro.stream()
                .filter(v -> "ANTES_0530".equals(v.faixaHorarioSaida())).toList());
        model.addAttribute("passageirosPendentesNaoCarro", agenda.passageirosPendentes().stream()
                .filter(p -> !TipoVeiculoUtil.ehCarro(nomesPorTipo.get(p.idTipoVeiculo())))
                .toList());
        model.addAttribute("passageirosPendentesCarro", agenda.passageirosPendentes().stream()
                .filter(p -> TipoVeiculoUtil.ehCarro(nomesPorTipo.get(p.idTipoVeiculo())))
                .toList());
        model.addAttribute("passageirosPendentesPorVeiculo", veiculosCarro.stream()
                .collect(Collectors.toMap(VeiculoAbertoResponseDto::id, veiculo ->
                        agenda.passageirosPendentes().stream()
                                .filter(p -> veiculo.idListaAbertura() == null
                                        || veiculo.idListaAbertura().equals(p.idLista()))
                                .toList())));
        model.addAttribute("veiculosDisponiveisParaAtribuicaoManual", agenda.passageirosPendentes().stream()
                .collect(Collectors.toMap(
                        AgendamentoResponseDto::id,
                        p -> agenda.veiculos().stream()
                                .filter(v -> v.capacidade() - v.assentosOcupados() >= p.acompanhantes() + 1)
                                .map(VeiculoAbertoResponseDto::id)
                                .toList())));
    }

    /** Lista única de passageiros + mapa passageiro -> veículo atribuído (página inicial). */
    public void consolidarPassageiros(Model model, AgendaDiaResponseDto agenda) {
        List<AgendamentoResponseDto> todos = new ArrayList<>();
        Map<UUID, VeiculoAtribuidoView> veiculoAtribuido = new HashMap<>();

        for (var veiculo : agenda.veiculos()) {
            var view = new VeiculoAtribuidoView(nomeDoVeiculo(veiculo), veiculo.horarioSaida());
            for (var passageiro : veiculo.passageiros()) {
                todos.add(passageiro);
                veiculoAtribuido.put(passageiro.id(), view);
            }
        }
        todos.addAll(agenda.passageirosPendentes());

        model.addAttribute("passageirosCadastrados", todos);
        model.addAttribute("veiculoAtribuido", veiculoAtribuido);
    }

    private static String nomeDoVeiculo(VeiculoAbertoResponseDto veiculo) {
        return veiculo.identificacao() == null
                ? veiculo.nomeTipoVeiculo()
                : veiculo.identificacao() + " — " + veiculo.nomeTipoVeiculo();
    }
}
