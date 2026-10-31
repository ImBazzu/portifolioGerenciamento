package br.com.imbazzu.secretariaTransporte.lista.core.porta.out;

import java.util.Optional;
import java.util.UUID;

import br.com.imbazzu.secretariaTransporte.compartilhados.domain.pagination.ResultadoPaginado;
import br.com.imbazzu.secretariaTransporte.lista.core.model.TipoListaDetalhe;
import br.com.imbazzu.secretariaTransporte.lista.core.model.TipoListaResumo;

public interface TipoListaPortaQuery {
    

    Optional<TipoListaDetalhe> buscarPorId(UUID id);

    ResultadoPaginado<TipoListaResumo> listarAtivos(String nome, int pagina, int tamanho);

    ResultadoPaginado<TipoListaResumo> listarArquivados(String nome, int pagina, int tamanho);
}