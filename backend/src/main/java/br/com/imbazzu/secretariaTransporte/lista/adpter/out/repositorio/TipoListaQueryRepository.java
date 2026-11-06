package br.com.imbazzu.secretariaTransporte.lista.adpter.out.repositorio;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import br.com.imbazzu.secretariaTransporte.lista.adpter.out.entidadeJpa.TipoListaJpa;
import br.com.imbazzu.secretariaTransporte.lista.adpter.out.repositorio.projecoes.TipoListaDetalheProjecao;
import br.com.imbazzu.secretariaTransporte.lista.adpter.out.repositorio.projecoes.TipoListaResumoProjecao;

/**
 * Repositório do lado Query: estende Repository (e não JpaRepository) de propósito,
 * assim nenhum método de escrita (save/delete) fica exposto aqui.
 */
public interface TipoListaQueryRepository extends Repository<TipoListaJpa, UUID> {

    @EntityGraph(attributePaths = "regrasSeparacao.tipoVeiculo")
    Optional<TipoListaDetalheProjecao> findProjectedById(UUID id);

    @Query(value = """
            SELECT l.id AS id, l.nome AS nome, l.descricao AS descricao, l.dataArquivado AS dataArquivado,
                   (SELECT COUNT(rc) FROM RegraCadastroJpa rc WHERE rc.lista.id = l.id) +
                   (SELECT COUNT(rs) FROM RegraSeparacaoJpa rs WHERE rs.lista.id = l.id) AS totalRegras,
                   (SELECT COUNT(DISTINCT cidade)
                    FROM RegraCadastroJpa rc JOIN rc.idsCidades cidade
                    WHERE rc.lista.id = l.id) AS totalCidades
            FROM TipoListaJpa l
            WHERE l.dataArquivado IS NULL
              AND LOWER(l.nome) LIKE LOWER(CONCAT('%', :nome, '%'))
            ORDER BY l.nome ASC
            """, countQuery = """
            SELECT COUNT(l)
            FROM TipoListaJpa l
            WHERE l.dataArquivado IS NULL
              AND LOWER(l.nome) LIKE LOWER(CONCAT('%', :nome, '%'))
            """)
    Page<TipoListaResumoProjecao> listarAtivos(@Param("nome") String nome, Pageable pageable);

    @Query(value = """
            SELECT l.id AS id, l.nome AS nome, l.descricao AS descricao, l.dataArquivado AS dataArquivado,
                   (SELECT COUNT(rc) FROM RegraCadastroJpa rc WHERE rc.lista.id = l.id) +
                   (SELECT COUNT(rs) FROM RegraSeparacaoJpa rs WHERE rs.lista.id = l.id) AS totalRegras,
                   (SELECT COUNT(DISTINCT cidade)
                    FROM RegraCadastroJpa rc JOIN rc.idsCidades cidade
                    WHERE rc.lista.id = l.id) AS totalCidades
            FROM TipoListaJpa l
            WHERE l.dataArquivado IS NOT NULL
              AND LOWER(l.nome) LIKE LOWER(CONCAT('%', :nome, '%'))
            ORDER BY l.nome ASC
            """, countQuery = """
            SELECT COUNT(l)
            FROM TipoListaJpa l
            WHERE l.dataArquivado IS NOT NULL
              AND LOWER(l.nome) LIKE LOWER(CONCAT('%', :nome, '%'))
            """)
    Page<TipoListaResumoProjecao> listarArquivados(@Param("nome") String nome, Pageable pageable);
}
