package br.com.imbazzu.secretariaTransporte.cadastrais.adapter.in.controller.dto;

import java.util.List;
import java.util.UUID;

import br.com.imbazzu.secretariaTransporte.tipoVeiculo.adpter.in.controller.dto.TipoVeiculoResponseDto;

public record MotoristaResponseDto(
                UUID id,
                String nome,
                String cpf,
                String telefone,
                List<TipoVeiculoResponseDto> tipoMotorista) {

}
