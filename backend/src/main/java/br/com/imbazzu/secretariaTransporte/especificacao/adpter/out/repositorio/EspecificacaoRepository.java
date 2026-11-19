package br.com.imbazzu.secretariaTransporte.especificacao.adpter.out.repositorio;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.imbazzu.secretariaTransporte.especificacao.adpter.out.entidadeJpa.EspecificacaoJpa;

@Repository
public interface EspecificacaoRepository extends JpaRepository<EspecificacaoJpa, UUID> {

    Optional<EspecificacaoJpa> findByIdAndDataArquivadoIsNull(UUID id);

    Optional<EspecificacaoJpa> findByIdAndDataArquivadoIsNotNull(UUID id);

    Page<EspecificacaoJpa> findByNomeContainingIgnoreCaseAndDataArquivadoIsNull(String nome, Pageable pageable);

    Page<EspecificacaoJpa> findByNomeContainingIgnoreCaseAndDataArquivadoIsNotNull(String nome, Pageable pageable);

    List<EspecificacaoJpa> findByDataArquivadoIsNull();

    boolean existsByNome(String nome);

    boolean existsByNomeAndIdNot(String nome, UUID id);
}
