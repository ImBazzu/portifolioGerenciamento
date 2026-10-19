package br.com.imbazzu.secretariaTransporte.lista.adapter.in.controller.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

/**
 * Dados para adicionar ou remover uma cidade de um conjunto existente.
 */
public record ConjuntosRequestDto(
        @NotNull(message = "Id da cidade é obrigatório")
        UUID idCidade
) {}
