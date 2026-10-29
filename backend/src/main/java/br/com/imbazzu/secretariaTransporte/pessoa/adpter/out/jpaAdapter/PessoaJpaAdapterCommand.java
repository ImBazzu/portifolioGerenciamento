package br.com.imbazzu.secretariaTransporte.pessoa.adpter.out.jpaAdapter;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.com.imbazzu.secretariaTransporte.pessoa.adpter.out.entidadeJpa.PessoaJpa;
import br.com.imbazzu.secretariaTransporte.pessoa.adpter.out.mapper.PessoaBancoMapper;
import br.com.imbazzu.secretariaTransporte.pessoa.adpter.out.repositorio.PessoaRepository;
import br.com.imbazzu.secretariaTransporte.pessoa.core.domain.Pessoa;
import br.com.imbazzu.secretariaTransporte.pessoa.core.porta.out.PessoaCommandPorta;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PessoaJpaAdapterCommand implements PessoaCommandPorta {

    private final PessoaRepository repository;

    @Override
    @Transactional
    public UUID salvar(Pessoa pessoa) {
        var entidade = repository.findById(pessoa.getId()).orElseGet(PessoaJpa::new);
        PessoaBancoMapper.atualizarEntidade(pessoa, entidade);
        return repository.saveAndFlush(entidade).getId();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Pessoa> buscarPorId(UUID id) {
        return repository.findById(id).map(PessoaBancoMapper::paraDomain);
    }


    @Override
    @Transactional(readOnly = true)
    public boolean existeCpf(String cpf, UUID ignorarId) {
        return ignorarId == null
                ? repository.existsByCpf(cpf)
                : repository.existsByCpfAndIdNot(cpf, ignorarId);
    }

    @Override
    @Transactional
    public void deletar(UUID id) {
        repository.deleteById(id);
    }
}
