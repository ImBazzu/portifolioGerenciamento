package br.com.imbazzu.secretariaTransporte.cadastrais.adapter.in.controller.dto;

import java.util.UUID;

public record DestinoResponseDto(
                UUID id,
                String nome,
                Integer qtdPassageiros) {
}
