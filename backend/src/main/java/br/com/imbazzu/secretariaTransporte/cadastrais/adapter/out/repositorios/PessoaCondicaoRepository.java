package br.com.imbazzu.secretariaTransporte.cadastrais.adapter.out.repositorios;

import br.com.imbazzu.secretariaTransporte.cadastrais.adapter.out.entidadesJpa.PessoaCondicaoJpa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface PessoaCondicaoRepository extends JpaRepository<PessoaCondicaoJpa, UUID> {

    @Query("Select p FROM PessoaCondicaoJpa p WHERE p.nome LIKE CONCAT('%',:nome,'%')")
    Page<PessoaCondicaoJpa> buscarPorNome(String nome, Pageable pageable);

    boolean existsByNome(String nome);


    boolean existsByNomeAndIdIgnoreCase(String nome, UUID id);
}
