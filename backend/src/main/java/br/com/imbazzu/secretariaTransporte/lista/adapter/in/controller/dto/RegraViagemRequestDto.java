package br.com.imbazzu.secretariaTransporte.lista.adapter.in.controller.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Dados para atualizar as configurações globais da regra.
 */
public record RegraViagemRequestDto(@NotNull @Min(1)
                                    Integer capacidadeMaxima,
                                    @NotNull @Min(0)
                                    Integer toleranciaHora,
                                    @NotNull @Min(0)
                                    Integer toleranciaMinuto) {
}
