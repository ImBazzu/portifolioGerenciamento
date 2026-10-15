package br.com.imbazzu.secretariaTransporte.localidades.application.query.destino.listarArquivado;

import java.util.UUID;
import java.util.List;

import br.com.imbazzu.secretariaTransporte.compartilhados.domain.pagination.OrdemPaginacao;

public record DestinoQueryListarArquivado(
        UUID idCidade, String nome, int pagina, int tamanho, List<OrdemPaginacao> ordenacao) {
}
