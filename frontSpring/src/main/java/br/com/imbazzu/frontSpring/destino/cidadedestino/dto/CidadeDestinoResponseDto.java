package br.com.imbazzu.frontSpring.destino.cidadedestino.dto;

import java.util.UUID;

/**
 * CidadeDestinoResponseDto
 */
public record CidadeDestinoResponseDto(UUID id, String nome, String estado, String tempoViagem) {

}
