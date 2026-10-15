package br.com.imbazzu.secretariaTransporte.cadastrais.adapter.out.jpaAdapters;

import br.com.imbazzu.secretariaTransporte.cadastrais.adapter.out.mapper.PessoaBancoMapper;
import br.com.imbazzu.secretariaTransporte.cadastrais.adapter.out.repositorios.PessoaRepository;
import br.com.imbazzu.secretariaTransporte.compartilhados.adapter.outbound.persistence.mapper.PageMapper;
import br.com.imbazzu.secretariaTransporte.compartilhados.domain.pagination.ResultadoPaginado;
import br.com.imbazzu.secretariaTransporte.cadastrais.core.model.Pessoa;
import br.com.imbazzu.secretariaTransporte.cadastrais.core.porta.out.PessoaRepositorioPorta;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class PessoaJpaAdapter implements PessoaRepositorioPorta {

    private final PessoaRepository banco;

    public PessoaJpaAdapter(PessoaRepository banco) {
        this.banco = banco;
    }

    @Override
    public Optional<Pessoa> buscarPorId(UUID idPessoa) {
        var pessoaJpa = banco.findById(idPessoa);
        return pessoaJpa.map(PessoaBancoMapper::paraDomainPessoa);
    }

    @Override
    public UUID salvar(Pessoa pessoa) {
        var pessoaJpa = PessoaBancoMapper.paraEntityJpa(pessoa);
        var resultado = banco.save(pessoaJpa);
        return resultado.getId();
    }

    @Override
    public boolean verificarExistenciaCpf(String cpf) {
        return banco.existsByCpf(cpf);
    }

    @Override
    public boolean verificarExistenciaCpfIgnorandoId(String cpf, UUID idPessoa) {
        return banco.existsByCpfAndIdIgnoreCase(cpf,idPessoa);
    }

    @Override
    public ResultadoPaginado<Pessoa> buscarPorNome(String nome, int pagina, int tamanho) {
        Pageable  pageable = PageRequest.of(pagina, tamanho);
        var result = banco.findByNome(nome, pageable).map(PessoaBancoMapper::paraDomainPessoa);
        return PageMapper.toDomain(result);
    }
}
