package br.com.imbazzu.secretariaTransporte.cadastrais.adapter.out.repositorios;

import br.com.imbazzu.secretariaTransporte.cadastrais.adapter.out.entidadesJpa.PessoaJpa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface PessoaRepository extends JpaRepository<PessoaJpa, UUID> {

    boolean existsByCpf(String cpf);

    boolean existsByCpfAndIdIgnoreCase(String cpf, UUID idPessoa);

    @Query("SELECT p FROM PessoaJpa p WHERE p.nome like CONCAT('%',:nome,'%')")
    Page<PessoaJpa> findByNome(String nome, Pageable pageable);
}
