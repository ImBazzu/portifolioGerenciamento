package br.com.imbazzu.secretariaTransporte.localidades.application.query.cidade.listar;

import java.util.List;

import br.com.imbazzu.secretariaTransporte.compartilhados.domain.pagination.OrdemPaginacao;

public record CidadeQueryListar(String nome, int pagina, int tamanhoPagina, List<OrdemPaginacao> ordenacao) {
}
