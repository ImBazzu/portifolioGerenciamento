package br.com.imbazzu.secretariaTransporte.lista.adpter.in.controller.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import br.com.imbazzu.secretariaTransporte.lista.core.domain.EnumPrioridade;

public record TipoListaResponseDto(
        UUID id,
        String nome,
        String descricao,
        LocalDate dataArquivado,
        List<RegraCadastroResponseDto> regrasCadastro,
        List<RegraSeparacaoResponseDto> regrasSeparacao) {

    public record RegraCadastroResponseDto(
            UUID id,
            List<VeiculoResponseDto> veiculos,
            List<CidadeResponseDto> cidades,
            List<EnumPrioridade> prioridades,
            LocalTime inicioHorario,
            LocalTime finalHorario,
            boolean obrigacaoAnexo,
            String descricao) {
    }

    public record RegraSeparacaoResponseDto(
            UUID id,
            UUID idTipoVeiculo,
            long tempoToleranciaSegundos,
            int tempoToleranciaNanos) {
    }

    public record CidadeResponseDto(UUID id, String nome){}

    public record VeiculoResponseDto(UUID id, String nome){}
}
