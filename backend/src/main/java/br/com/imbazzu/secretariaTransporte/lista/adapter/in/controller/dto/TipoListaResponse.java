package br.com.imbazzu.secretariaTransporte.lista.lista.tipoLista.dto;


import java.util.List;
import java.util.UUID;

public record TipoListaResponse(
        UUID id,
        String nome,
        String descricao,
        List<TipoListaCidadesResponseDto> cidades
) {
}
