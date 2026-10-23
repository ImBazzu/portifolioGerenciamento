package br.com.imbazzu.secretariaTransporte.lista.adpter.in.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TipoListaRequestDto(
        @NotBlank @Size(min = 3, max = 59) String nome,
        @Size(max = 249) String descricao) {
}
