package br.com.imbazzu.secretariaTransporte.lista.adpter.out.jpaAdapter;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.com.imbazzu.secretariaTransporte.lista.adpter.out.entidadeJpa.TipoListaJpa;
import br.com.imbazzu.secretariaTransporte.lista.adpter.out.mapper.TipoListaBancoMapper;
import br.com.imbazzu.secretariaTransporte.lista.adpter.out.repositorio.TipoListaRepository;
import br.com.imbazzu.secretariaTransporte.lista.core.domain.TipoLista;
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
    @Transactional
    public void deletar(UUID id) {
        repository.deleteById(id);
    }
}
