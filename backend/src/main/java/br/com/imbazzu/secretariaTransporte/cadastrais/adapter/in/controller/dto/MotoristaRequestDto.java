package br.com.imbazzu.secretariaTransporte.cadastrais.adapter.in.controller.dto;


import java.util.List;
import java.util.UUID;

public record MotoristaRequestDto(

        String nome,
        String cpf,
        String telefone,
        List<UUID> idVeiculos) {
}
