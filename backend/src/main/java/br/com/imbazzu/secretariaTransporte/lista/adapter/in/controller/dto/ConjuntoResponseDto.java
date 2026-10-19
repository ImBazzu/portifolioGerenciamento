package br.com.imbazzu.secretariaTransporte.lista.adapter.in.controller.dto;

import java.util.List;
import java.util.UUID;

public record ConjuntoResponseDto(UUID id, String nome, List<String> cidades) {
}
