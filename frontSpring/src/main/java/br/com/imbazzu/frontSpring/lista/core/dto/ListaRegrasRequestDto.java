package br.com.imbazzu.frontSpring.lista.core.dto;

import java.util.List;
import java.util.UUID;

public record ListaRegrasRequestDto(
        List<RegraCadastro> regrasCadastro,
        List<RegraSeparacao> regrasSeparacao) {

    public record RegraCadastro(
            List<UUID> idsVeiculos,
            List<UUID> idsCidades,
            List<String> prioridades,
            String inicioHorario,
            String finalHorario,
            boolean obrigacaoAnexo,
            String descricao) {
    }

    public record RegraSeparacao(
            UUID idTipoVeiculo,
            long tempoToleranciaSegundos,
            int tempoToleranciaNanos) {
    }
}
