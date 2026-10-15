package br.com.imbazzu.secretariaTransporte.localidades.adapters.in.dto;

import java.util.UUID;

public record CidadeResponseDto(
    UUID id, 
    String nome, 
    String estado,
     String tempoViagem,
    Integer totalDestinos) {
}
