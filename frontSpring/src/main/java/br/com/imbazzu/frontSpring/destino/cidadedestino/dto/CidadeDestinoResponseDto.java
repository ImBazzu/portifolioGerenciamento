package br.com.imbazzu.frontspring.destino.cidadedestino.dto;

import java.util.UUID;

/**
 * CidadeDestinoResponseDto
 */
public record CidadeDestinoResponseDto(UUID id, String nome, String estado, String tempoViagem) {

}
