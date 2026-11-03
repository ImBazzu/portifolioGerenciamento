package br.com.imbazzu.secretariaTransporte.lista.application.query.buscar;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import br.com.imbazzu.secretariaTransporte.lista.core.domain.EnumPrioridade;

/**
 * Resultado da query de busca: já com os nomes de cidades e veículos resolvidos.
 */
public record ListaQueryBuscarResultado(
        UUID id,
        String nome,
        String descricao,
        LocalDate dataArquivado,
        List<RegraCadastroResultado> regrasCadastro,
        List<RegraSeparacaoResultado> regrasSeparacao) {

    public record RegraCadastroResultado(
            UUID id,
            List<IdNome> veiculos,
            List<IdNome> cidades,
            List<EnumPrioridade> prioridades,
            LocalTime inicioHorario,
            LocalTime finalHorario,
            boolean obrigacaoAnexo,
            String descricao) {
    }

    public record RegraSeparacaoResultado(
            UUID id,
            UUID idTipoVeiculo,
            String nome,
            String tempoTolerancia,
            long tempoToleranciaSegundos,
            int tempoToleranciaNanos) {
    }

    public record IdNome(UUID id, String nome) {
    }
}
