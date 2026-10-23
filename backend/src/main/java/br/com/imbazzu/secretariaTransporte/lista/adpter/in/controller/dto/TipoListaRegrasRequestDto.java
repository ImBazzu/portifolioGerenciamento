package br.com.imbazzu.secretariaTransporte.lista.adpter.in.controller.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record TipoListaRegrasRequestDto(
        @NotNull List<@Valid RegraCadastroRequestDto> regrasCadastro,
        @NotNull List<@Valid RegraSeparacaoRequestDto> regrasSeparacao) {
}
