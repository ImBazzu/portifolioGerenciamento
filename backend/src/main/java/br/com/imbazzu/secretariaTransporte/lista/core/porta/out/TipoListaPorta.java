package br.com.imbazzu.secretariaTransporte.lista.core.porta.out;

import java.util.Optional;
import java.util.UUID;

import br.com.imbazzu.secretariaTransporte.compartilhados.domain.pagination.ResultadoPaginado;
import br.com.imbazzu.secretariaTransporte.lista.core.domain.TipoLista;
import br.com.imbazzu.secretariaTransporte.lista.core.model.TipoListaResumo;

public interface TipoListaPorta {

    UUID salvar(TipoLista lista);

    Optional<TipoLista> buscarPorId(UUID id);

    ResultadoPaginado<TipoListaResumo> listar(String nome, boolean arquivado, int pagina, int tamanho);

    void deletar(UUID id);
}
