package br.com.imbazzu.secretariaTransporte.localidades.application.query.destino.listarArquivado;

import java.util.UUID;

public record DestinoQueryListarArquivado(UUID idCidade, String nome, int pagina, int tamanho) {

}
