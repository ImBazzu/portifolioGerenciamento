package br.com.imbazzu.secretariaTransporte.especificacao.adpter.out.jpaAdapter;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.com.imbazzu.secretariaTransporte.especificacao.adpter.out.entidadeJpa.EspecificacaoJpa;
import br.com.imbazzu.secretariaTransporte.especificacao.adpter.out.mapper.EspecificacaoBancoMapper;
import br.com.imbazzu.secretariaTransporte.especificacao.adpter.out.repositorio.EspecificacaoRepository;
import br.com.imbazzu.secretariaTransporte.especificacao.core.domain.Especificacao;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class EspecificacaoCommandAdapter {

    private final EspecificacaoRepository repository;

    @Transactional
    public UUID salvar(Especificacao especificacao) {
        var entidade = repository.findById(especificacao.getId())
                .orElseGet(EspecificacaoJpa::new);
        EspecificacaoBancoMapper.atualizarEntidade(especificacao, entidade);
        return repository.saveAndFlush(entidade).getId();
    }

    @Transactional(readOnly = true)
    public Optional<Especificacao> buscarPorId(UUID id) {
        return repository.findById(id)
                .map(EspecificacaoBancoMapper::paraDomain);
    }

    @Transactional(readOnly = true)
    public boolean existeNome(String nome, UUID ignorarId) {
        return ignorarId == null
                ? repository.existsByNome(nome)
                : repository.existsByNomeAndIdNot(nome, ignorarId);
    }

    @Transactional
    public void deletar(UUID id) {
        repository.deleteById(id);
    }
}
