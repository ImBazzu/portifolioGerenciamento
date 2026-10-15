package br.com.imbazzu.secretariaTransporte.localidades.application.query.cidade.listarArquivado;

import java.util.List;

import br.com.imbazzu.secretariaTransporte.compartilhados.domain.pagination.OrdemPaginacao;

public record CidadeQueryListarArquivado(
        String nome, int pagina, int tamanhoPagina, List<OrdemPaginacao> ordenacao) {
}
