package br.com.imbazzu.secretariaTransporte.lista.lista.tipoLista.dto;

import java.util.List;
import java.util.UUID;

public record TipoListaCidadesResponseDto(
        UUID id,
        String cidade,
        List<TipoListaDestinosResponseDto> destinos) {
}
