package br.com.imbazzu.secretariaTransporte.cadastrais.adapter.out.jpaAdapters;

import br.com.imbazzu.secretariaTransporte.cadastrais.adapter.out.entidadesJpa.PessoaCondicaoJpa;
import br.com.imbazzu.secretariaTransporte.cadastrais.adapter.out.repositorios.PessoaCondicaoRepository;
import br.com.imbazzu.secretariaTransporte.compartilhados.adapter.outbound.persistence.mapper.PageMapper;
import br.com.imbazzu.secretariaTransporte.compartilhados.domain.pagination.ResultadoPaginado;
import br.com.imbazzu.secretariaTransporte.compartilhados.valueObjects.Nome;
import br.com.imbazzu.secretariaTransporte.cadastrais.core.model.PessoaCondicao;
import br.com.imbazzu.secretariaTransporte.cadastrais.core.porta.out.PessoaCondicaoRepositorioPorta;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class PessoaCondicaoJpaAdapter implements PessoaCondicaoRepositorioPorta {

    private final PessoaCondicaoRepository banco;

    public PessoaCondicaoJpaAdapter(PessoaCondicaoRepository banco) {
        this.banco = banco;
    }

    @Override
    public Optional<PessoaCondicao> buscarPorId(UUID pessoaCondicaoId) {
        return banco.findById(pessoaCondicaoId).map(
                p->{
                    return PessoaCondicao.recriar(p.getId(),new Nome(p.getNome()),p.isArquivado());}
        );
    }

    @Override
    public UUID salvar(PessoaCondicao pessoaCondicao) {
        var pessoaCondicaoBanco = new PessoaCondicaoJpa(pessoaCondicao);
        var pessoaSalva = banco.save(pessoaCondicaoBanco);
        return pessoaSalva.getId();
    }

    @Override
    public ResultadoPaginado<PessoaCondicao> buscarPorNome(String nome, int pagina, int tamanho) {
        var pageable = PageRequest.of(pagina, tamanho);
        var paginado = banco.buscarPorNome(nome,pageable).map(p->{
            return PessoaCondicao.recriar(p.getId(),new Nome(p.getNome()),p.isArquivado());
        });
        return PageMapper.toDomain(paginado);
    }

    @Override
    public boolean existePorNome(String nome) {
        return banco.existsByNome(nome);
    }

    @Override
    public boolean existePorNomeIgnorandoId(String nome, UUID pessoaCondicaoId) {
        return banco.existsByNomeAndIdIgnoreCase(nome,pessoaCondicaoId);
    }
}
