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

        public List<UUID> getIdsVeiculos() {
            return veiculos.stream().map(VeiculoResponse::id).toList();
        }

        public List<UUID> getIdsCidades() {
            return cidades.stream().map(CidadeResponse::id).toList();
        }
    }

    public record RegraSeparacao(
            UUID id,
            UUID idTipoVeiculo,
            String nome,
            String tempoTolerancia,
            long tempoToleranciaSegundos,
            int tempoToleranciaNanos) {
    }

    public record CidadeResponse(
        UUID id,
        String nome
    ){}

    public record VeiculoResponse(
        UUID id,
        String nome
    ){}
}
