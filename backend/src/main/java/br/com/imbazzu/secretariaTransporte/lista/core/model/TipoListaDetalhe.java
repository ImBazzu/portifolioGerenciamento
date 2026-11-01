package br.com.imbazzu.secretariaTransporte.lista.core.model;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import br.com.imbazzu.secretariaTransporte.lista.core.domain.EnumPrioridade;

/**
 * Modelo de leitura (lado Query): só ids, sem nomes de cidade/veículo e sem regras de negócio.
 * Quem enriquece com nomes é o handler da query.
 */
public record TipoListaDetalhe(
        UUID id,
        String nome,
        String descricao,
        LocalDate dataArquivado,
        List<RegraCadastroDetalhe> regrasCadastro,
        List<RegraSeparacaoDetalhe> regrasSeparacao) {

    public record RegraCadastroDetalhe(
            UUID id,
            List<UUID> idsVeiculos,
            List<UUID> idsCidades,
            List<EnumPrioridade> prioridades,
            LocalTime inicioHorario,
            LocalTime finalHorario,
            boolean obrigacaoAnexo,
            String descricao) {
    }

    public record RegraSeparacaoDetalhe(UUID id, String nome, String tempoTolerancia) {
    }
}
