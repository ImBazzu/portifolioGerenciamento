package br.com.imbazzu.secretariaTransporte.veiculo.application.query.historico;

import java.util.UUID;

public record TipoVeiculoQueryHistorico(
        UUID idTipoVeiculo,
        int pagina,
        int tamanho) {

}
