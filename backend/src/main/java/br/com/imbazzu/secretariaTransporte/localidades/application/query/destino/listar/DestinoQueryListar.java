package br.com.imbazzu.secretariaTransporte.localidades.application.query.destino.listar;

import java.util.UUID;
import java.util.List;

import br.com.imbazzu.secretariaTransporte.compartilhados.domain.pagination.OrdemPaginacao;

public record DestinoQueryListar(
        UUID idCidade, String nomeDestino, int pagina, int tamanho, List<OrdemPaginacao> ordenacao) {
}
