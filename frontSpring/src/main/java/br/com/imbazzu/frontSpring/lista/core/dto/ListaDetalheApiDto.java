package br.com.imbazzu.frontSpring.lista.core.dto;

import java.util.List;
import java.util.UUID;

public record ListaDetalheApiDto(
        UUID id,
        String nome,
        String descricao,
        String dataArquivado,
        List<RegraCadastro> regrasCadastro,
        List<RegraSeparacao> regrasSeparacao) {

    public record RegraCadastro(
            UUID id,
            List<VeiculoResponse> veiculos,
            List<CidadeResponse> cidades,
            List<String> prioridades,
            String inicioHorario,
            String finalHorario,
            boolean obrigacaoAnexo,
            String descricao) {
    }

    public record RegraSeparacao(
            UUID id,
            String nome,
            String tempoTolerancia) {
    }

    public record CidadeResponse(
        UUID ids,
        String nome
    ){}

    public record VeiculoResponse(
        UUID ids,
        String nome
    ){}
}
