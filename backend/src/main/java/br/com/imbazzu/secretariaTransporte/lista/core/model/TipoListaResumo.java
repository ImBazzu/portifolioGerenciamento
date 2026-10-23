package br.com.imbazzu.secretariaTransporte.lista.core.model;

import java.time.LocalDate;
import java.util.UUID;

public record TipoListaResumo(
        UUID id,
        String nome,
        String descricao,
        LocalDate dataArquivado,
        long totalRegras,
        long totalCidades) {
}
