package br.com.imbazzu.secretariaTransporte.compartilhados.adapter.outbound.persistence.mapper;

import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import br.com.imbazzu.secretariaTransporte.compartilhados.domain.pagination.OrdemPaginacao;

public final class PageRequestMapper {

    private PageRequestMapper() {
    }

    public static PageRequest toPageRequest(int pagina, int tamanho, List<OrdemPaginacao> ordenacao) {
        var orders = Objects.requireNonNull(ordenacao, "Ordenação é obrigatória").stream()
                .map(order -> new Sort.Order(
                        order.ascendente() ? Sort.Direction.ASC : Sort.Direction.DESC,
                        order.propriedade()))
                .toList();
        var sort = orders.isEmpty() ? Sort.unsorted() : Sort.by(orders);
        return PageRequest.of(pagina, tamanho, sort);
    }
}
