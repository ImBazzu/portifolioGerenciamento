package br.com.imbazzu.secretariaTransporte.localidades.application.query.destino.listar;

import java.util.UUID;

public record DestinoQueryListar(UUID idCidade, String nomeDestino, int pagina, int tamanho) {

}
