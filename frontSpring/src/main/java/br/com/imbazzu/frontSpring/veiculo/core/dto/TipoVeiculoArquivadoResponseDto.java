package br.com.imbazzu.frontspring.veiculo.core.dto;

import java.time.LocalDate;
import java.util.UUID;

public record TipoVeiculoArquivadoResponseDto(
        UUID id,
        String nome,
        Integer capacidade,
        LocalDate dataArquivado) {

}

