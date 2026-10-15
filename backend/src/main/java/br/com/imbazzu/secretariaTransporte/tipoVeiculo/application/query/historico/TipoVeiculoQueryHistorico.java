package br.com.imbazzu.secretariaTransporte.tipoVeiculo.application.query.historico;

import java.util.UUID;

public record TipoVeiculoQueryHistorico(
        UUID idVeiculo,
        int pagina,
        int tamanho) {

}
