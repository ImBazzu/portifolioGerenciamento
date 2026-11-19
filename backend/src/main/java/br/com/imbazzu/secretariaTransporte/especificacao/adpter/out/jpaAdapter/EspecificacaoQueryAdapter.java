package br.com.imbazzu.secretariaTransporte.especificacao.adpter.out.jpaAdapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.com.imbazzu.secretariaTransporte.compartilhados.adapter.outbound.persistence.mapper.PageMapper;
import br.com.imbazzu.secretariaTransporte.compartilhados.domain.pagination.ResultadoPaginado;
import br.com.imbazzu.secretariaTransporte.especificacao.adpter.out.entidadeJpa.EspecificacaoJpa;
import br.com.imbazzu.secretariaTransporte.especificacao.adpter.out.mapper.EspecificacaoBancoMapper;
import br.com.imbazzu.secretariaTransporte.especificacao.adpter.out.repositorio.EspecificacaoRepository;
import br.com.imbazzu.secretariaTransporte.especificacao.core.domain.Especificacao;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class EspecificacaoQueryAdapter {

    private final EspecificacaoRepository repository;

    @Transactional(readOnly = true)
    public Optional<Especificacao> buscar(UUID id) {
        return repository.findByIdAndDataArquivadoIsNull(id)
                .map(EspecificacaoBancoMapper::paraDomain);
    }

    @Transactional(readOnly = true)
    public Optional<Especificacao> buscarArquivado(UUID id) {
        return repository.findByIdAndDataArquivadoIsNotNull(id)
                .map(EspecificacaoBancoMapper::paraDomain);
    }

    @Transactional(readOnly = true)
    public ResultadoPaginado<Especificacao> listarAtivas(String nome, int pagina, int tamanho) {
        var pageable = PageRequest.of(pagina, tamanho);
        var resultado = repository.findByNomeContainingIgnoreCaseAndDataArquivadoIsNull(nome, pageable);
        return PageMapper.toDomain(resultado.map(EspecificacaoBancoMapper::paraDomain));
    }

    @Transactional(readOnly = true)
    public ResultadoPaginado<Especificacao> listarArquivadas(String nome, int pagina, int tamanho) {
        var pageable = PageRequest.of(pagina, tamanho);
        var resultado = repository.findByNomeContainingIgnoreCaseAndDataArquivadoIsNotNull(nome, pageable);
        return PageMapper.toDomain(resultado.map(EspecificacaoBancoMapper::paraDomain));
    }

    @Transactional(readOnly = true)
    public List<Especificacao> listarTodosAtivos() {
        return repository.findByDataArquivadoIsNull()
                .stream()
                .map(EspecificacaoBancoMapper::paraDomain)
                .collect(Collectors.toList());
    }
}
