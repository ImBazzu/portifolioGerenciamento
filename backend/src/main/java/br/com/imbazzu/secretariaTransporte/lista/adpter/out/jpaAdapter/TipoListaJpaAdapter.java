package br.com.imbazzu.secretariaTransporte.lista.adpter.out.jpaAdapter;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.com.imbazzu.secretariaTransporte.compartilhados.adapter.outbound.persistence.mapper.PageMapper;
import br.com.imbazzu.secretariaTransporte.compartilhados.domain.pagination.ResultadoPaginado;
import br.com.imbazzu.secretariaTransporte.lista.adpter.out.entidadeJpa.TipoListaJpa;
import br.com.imbazzu.secretariaTransporte.lista.adpter.out.mapper.TipoListaBancoMapper;
import br.com.imbazzu.secretariaTransporte.lista.adpter.out.repositorio.TipoListaRepository;
import br.com.imbazzu.secretariaTransporte.lista.core.domain.TipoLista;
import br.com.imbazzu.secretariaTransporte.lista.core.model.TipoListaResumo;
import br.com.imbazzu.secretariaTransporte.lista.core.porta.out.TipoListaPorta;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TipoListaJpaAdapter implements TipoListaPorta {

    private final TipoListaRepository repository;

    @Override
    @Transactional
    public UUID salvar(TipoLista lista) {
        var entidade = repository.findById(lista.getId()).orElseGet(TipoListaJpa::new);
        TipoListaBancoMapper.atualizarEntidade(lista, entidade);
        return repository.saveAndFlush(entidade).getId();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TipoLista> buscarPorId(UUID id) {
        return repository.findById(id).map(TipoListaBancoMapper::paraDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public ResultadoPaginado<TipoListaResumo> listar(String nome, boolean arquivado, int pagina, int tamanho) {
        var pageable = PageRequest.of(pagina, tamanho);
        var resultados = arquivado
                ? repository.listarArquivados(nome, pageable)
                : repository.listarAtivos(nome, pageable);
        return PageMapper.toDomain(resultados.map(TipoListaBancoMapper::paraResumo));
    }

    @Override
    @Transactional
    public void deletar(UUID id) {
        repository.deleteById(id);
    }
}
