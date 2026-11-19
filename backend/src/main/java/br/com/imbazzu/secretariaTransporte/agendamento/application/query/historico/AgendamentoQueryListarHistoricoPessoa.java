package br.com.imbazzu.secretariaTransporte.agendamento.application.query.historico;

import java.util.UUID;

public record AgendamentoQueryListarHistoricoPessoa(
        UUID idPessoa,
        String destinoBusca,
        int pagina,
        int tamanho) {

    public AgendamentoQueryListarHistoricoPessoa(UUID idPessoa, int pagina, int tamanho) {
        this(idPessoa, "", pagina, tamanho);
    }
}
