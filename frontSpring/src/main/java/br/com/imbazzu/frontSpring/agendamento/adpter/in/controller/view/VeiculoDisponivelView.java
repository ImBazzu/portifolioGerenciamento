package br.com.imbazzu.frontSpring.agendamento.adpter.in.controller.view;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import br.com.imbazzu.frontSpring.agendamento.core.dto.AgendaDiaResponseDto;
import br.com.imbazzu.frontSpring.agendamento.core.dto.VeiculoAbertoResponseDto;
import br.com.imbazzu.frontSpring.agendamento.core.dto.VeiculoPermitidoDto;
import br.com.imbazzu.frontSpring.agendamento.core.dto.VerificacaoVeiculosResponseDto;
import br.com.imbazzu.frontSpring.veiculo.core.TipoVeiculoUtil;
import br.com.imbazzu.frontSpring.veiculo.core.FinalidadeAberturaVeiculo;

public record VeiculoDisponivelView(
        UUID idTipoVeiculo,
        String nome,
        UUID idVeiculo,
        String identificacao,
        int lugaresDisponiveis) {

    public static List<VeiculoDisponivelView> disponiveis(
            VerificacaoVeiculosResponseDto verificacao,
            AgendaDiaResponseDto agenda,
            int acompanhantes) {
        if (verificacao == null || verificacao.veiculosPermitidos() == null) {
            return List.of();
        }

        return verificacao.veiculosPermitidos().stream()
                .flatMap(tipo -> veiculosDoTipo(tipo, agenda, acompanhantes))
                .toList();
    }

    public static List<VeiculoDisponivelView> disponiveis(
            VerificacaoVeiculosResponseDto verificacao,
            AgendaDiaResponseDto agenda,
            int acompanhantes,
            UUID veiculoOrigem) {
        if (veiculoOrigem == null) {
            return disponiveis(verificacao, agenda, acompanhantes);
        }
        if (agenda == null) {
            return List.of();
        }
        var origem = agenda.veiculos().stream()
                .filter(veiculo -> veiculo.id().equals(veiculoOrigem))
                .findFirst();
        if (origem.isEmpty()) {
            return List.of();
        }
        var veiculo = origem.get();
        if ((long) acompanhantes + 1 > veiculo.capacidade() - veiculo.assentosOcupados()) {
            return List.of();
        }
        if (veiculo.finalidadeAbertura() == FinalidadeAberturaVeiculo.AJUSTE
                && TipoVeiculoUtil.ehCarro(veiculo.nomeTipoVeiculo())) {
            return disponiveis(verificacao, agenda, acompanhantes).stream()
                    .filter(item -> item.idTipoVeiculo().equals(veiculo.idTipoVeiculo()))
                    .toList();
        }
        if (verificacao == null || verificacao.veiculosPermitidos() == null
                || verificacao.veiculosPermitidos().stream()
                        .noneMatch(tipo -> tipo.idTipoVeiculo().equals(veiculo.idTipoVeiculo()))) {
            return List.of();
        }
        return List.of(de(veiculo));
    }

    private static Stream<VeiculoDisponivelView> veiculosDoTipo(
            VeiculoPermitidoDto tipo,
            AgendaDiaResponseDto agenda,
            int acompanhantes) {
        if (TipoVeiculoUtil.ehCarro(tipo.nome())) {
            return Stream.of(new VeiculoDisponivelView(
                    tipo.idTipoVeiculo(), tipo.nome(), null, null, tipo.capacidade()));
        }
        if (agenda == null || agenda.veiculos() == null) {
            return Stream.empty();
        }

        int lugaresNecessarios = acompanhantes + 1;
        return agenda.veiculos().stream()
                .filter(veiculo -> mesmoTipo(tipo, veiculo))
                .filter(veiculo -> veiculo.capacidade() - veiculo.assentosOcupados() >= lugaresNecessarios)
                .map(VeiculoDisponivelView::de);
    }

    private static boolean mesmoTipo(VeiculoPermitidoDto tipo, VeiculoAbertoResponseDto veiculo) {
        return tipo.idTipoVeiculo().equals(veiculo.idTipoVeiculo());
    }

    private static VeiculoDisponivelView de(VeiculoAbertoResponseDto veiculo) {
        return new VeiculoDisponivelView(
                veiculo.idTipoVeiculo(),
                veiculo.nomeTipoVeiculo(),
                veiculo.id(),
                veiculo.identificacao(),
                veiculo.capacidade() - veiculo.assentosOcupados());
    }
}
