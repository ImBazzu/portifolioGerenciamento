package br.com.imbazzu.frontSpring.controller.dto;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public record ResultadoPaginado<T>(List<T> conteudo,
                int paginaAtual,
                int tamanhoPagina,
                long totalElementos,
                int totalPaginas) {

        public <R> ResultadoPaginado<R> map(
                        Function<? super T, ? extends R> mapper) {

                return new ResultadoPaginado<>(
                                conteudo.stream()
                                                .map(mapper)
                                                .collect(Collectors.toList()),
                                paginaAtual,
                                tamanhoPagina,
                                totalElementos,
                                totalPaginas);
        }

}
