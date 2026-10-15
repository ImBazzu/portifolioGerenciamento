package br.com.imbazzu.secretariaTransporte.tipoVeiculo.adpter.in.controller.dto;

import java.time.LocalDate;
import java.util.UUID;

public record TipoVeiculoArquivadoResponseDto(
        UUID id,
        String nome,
        Integer capacidade,
        LocalDate dataArquivado) {

}
