package br.com.imbazzu.secretariaTransporte.pessoa.adpter.out.jpaAdapter;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.com.imbazzu.secretariaTransporte.compartilhados.adapter.outbound.persistence.mapper.PageMapper;
import br.com.imbazzu.secretariaTransporte.compartilhados.domain.pagination.ResultadoPaginado;
import br.com.imbazzu.secretariaTransporte.pessoa.adpter.out.mapper.PessoaBancoMapper;
import br.com.imbazzu.secretariaTransporte.pessoa.adpter.out.repositorio.PessoaRepository;
import br.com.imbazzu.secretariaTransporte.pessoa.core.domain.Pessoa;
import br.com.imbazzu.secretariaTransporte.pessoa.core.porta.out.PessoaPorta;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PessoaJpaAdapter implements PessoaPorta {

    private final PessoaRepository repository;

    @Override
    @Transactional(readOnly = true)
    public Optional<Pessoa> buscar(UUID id) {
        return repository.findByIdAndDataArquivadoIsNull(id).map(PessoaBancoMapper::paraDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Pessoa> buscarArquivado(UUID id) {
        return repository.findByIdAndDataArquivadoIsNotNull(id).map(PessoaBancoMapper::paraDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public ResultadoPaginado<Pessoa> listar(String nome, boolean arquivado, int pagina, int tamanho) {
        var pageable = PageRequest.of(pagina, tamanho);
        var numero = nome.replaceAll("\\D", "");
        var resultados = arquivado
                ? repository.listarArquivadas(nome, numero, pageable)
                : repository.listarAtivas(nome, numero, pageable);
        return PageMapper.toDomain(resultados.map(PessoaBancoMapper::paraDomain));
    }

}
