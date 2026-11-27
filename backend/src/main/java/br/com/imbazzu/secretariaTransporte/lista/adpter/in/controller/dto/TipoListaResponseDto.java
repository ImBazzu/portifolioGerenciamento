package br.com.imbazzu.secretariaTransporte.lista.adpter.in.controller.dto;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;


public record TipoListaResponseDto(
        UUID id,
        String nome,
        String descricao,
        LocalDate dataArquivado,
        List<RegraCadastroResponseDto> regrasCadastro,
        List<RegraSeparacaoResponseDto> regrasSeparacao,
        String finalidadeEspecial) {

    public record RegraCadastroResponseDto(
            UUID id,
            List<VeiculoResponseDto> veiculos,
            List<CidadeResponseDto> cidades,
            List<UUID> prioridades,
            LocalTime inicioHorario,
            LocalTime finalHorario,
            boolean obrigacaoAnexo,
            String descricao,
            List<DayOfWeek> diasSemana,
            Integer intervaloRepetacao) {
    }

    public record RegraSeparacaoResponseDto(
            UUID id,
            UUID idTipoVeiculo,
            String nome,
            String tempoTolerancia,
            long tempoToleranciaSegundos,
            int tempoToleranciaNanos) {
    }

    public record CidadeResponseDto(UUID id, String nome){}

    public record VeiculoResponseDto(UUID id, String nome){}
}
