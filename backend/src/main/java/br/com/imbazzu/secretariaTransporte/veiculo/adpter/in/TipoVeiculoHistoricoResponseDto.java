package br.com.imbazzu.secretariaTransporte.veiculo.adpter.in;

import java.time.LocalDate;
import java.util.UUID;

public record TipoVeiculoHistoricoResponseDto(
        UUID id,
        LocalDate data,
        Integer quantidadeVeiculos,
        String destinos,
        Integer totalPassageiro) {

}
