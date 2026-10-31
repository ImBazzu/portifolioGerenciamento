package br.com.imbazzu.secretariaTransporte.lista.adpter.out.jpaAdapter;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.com.imbazzu.secretariaTransporte.compartilhados.adapter.outbound.persistence.mapper.PageMapper;
import br.com.imbazzu.secretariaTransporte.compartilhados.domain.pagination.ResultadoPaginado;
import br.com.imbazzu.secretariaTransporte.lista.adpter.out.mapper.TipoListaConsultaMapper;
import br.com.imbazzu.secretariaTransporte.lista.adpter.out.repositorio.TipoListaQueryRepository;
import br.com.imbazzu.secretariaTransporte.lista.core.model.TipoListaDetalhe;
import br.com.imbazzu.secretariaTransporte.lista.core.model.TipoListaResumo;
import br.com.imbazzu.secretariaTransporte.lista.core.porta.out.TipoListaPortaQuery;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TipoListaJpaQueryAdapter implements TipoListaPortaQuery {

    private final TipoListaQueryRepository repository;

    @Override
    @Transactional(readOnly = true)
    public Optional<TipoListaDetalhe> buscarPorId(UUID id) {
        // O mapeamento acontece aqui dentro da transação: as coleções lazy da projeção ainda estão acessíveis.
        return repository.findProjectedById(id).map(TipoListaConsultaMapper::paraDetalhe);
    }

    @Override
    @Transactional(readOnly = true)
    public ResultadoPaginado<TipoListaResumo> listarAtivos(String nome, int pagina, int tamanho) {
        var resultados = repository.listarAtivos(nome, PageRequest.of(pagina, tamanho));
        return PageMapper.toDomain(resultados.map(TipoListaConsultaMapper::paraResumo));
    }

    @Override
    @Transactional(readOnly = true)
    public ResultadoPaginado<TipoListaResumo> listarArquivados(String nome, int pagina, int tamanho) {
        var resultados = repository.listarArquivados(nome, PageRequest.of(pagina, tamanho));
        return PageMapper.toDomain(resultados.map(TipoListaConsultaMapper::paraResumo));
    }
}
