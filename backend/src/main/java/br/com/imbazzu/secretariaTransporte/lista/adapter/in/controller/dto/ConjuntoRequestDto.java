package br.com.imbazzu.secretariaTransporte.lista.adapter.in.controller.dto;

import java.util.Set;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Dados para criar um novo conjunto de cidades.
 */
public record ConjuntoRequestDto(
        @NotBlank(message = "Nome do conjunto é obrigatório")
        String nome,

        @NotNull(message = "Informe ao menos uma cidade")
        Set<UUID> idsCidades
) {}
