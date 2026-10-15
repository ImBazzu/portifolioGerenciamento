package br.com.imbazzu.secretariaTransporte.localidades.adapters.in.dto;

import java.util.UUID;

public record DestinoResponseDto(
                UUID id,
                String nome,
                Integer qtdPassageiros) {
}
