package br.com.imbazzu.secretariaTransporte.tipoVeiculo.adpter.out.repositorio;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.imbazzu.secretariaTransporte.tipoVeiculo.adpter.out.entidadeJpa.TipoVeiculoJpa;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.adpter.out.repositorio.projecoes.TipoVeiculoArquivadoProjecao;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.adpter.out.repositorio.projecoes.TipoVeiculoAtivoProjecao;

public interface TipoVeiculoQueryRepository extends JpaRepository<TipoVeiculoJpa, UUID> {


        //Busca por ID, retornando um Optional para lidar com a possibilidade de não encontrar o registro
        Optional<TipoVeiculoJpa> buscarPorId(UUID id);

        // Verifica se existe algum veículo usando o tipo de veículo especificado
        @Query ("""
                        SELECT CASE 
                                WHEN COUNT(v) > 0 THEN true 
                                ELSE false END
                        FROM VeiculoJpa v
                        WHERE v.idTipoVeiculo = :tipoVeiculoId
                """)
        boolean existeVeiculoUsandoTipoVeiculo(@Param("tipoVeiculoId") UUID tipoVeiculoId);

        // Verifica se existe outro tipo de veículo com o mesmo nome, ignorando o ID do veículo atual
        boolean existsByNomeAndIdNot(String nome, UUID id);

        // Verifica se existe outro tipo de veículo com o mesmo nome
        boolean existsByNome(String nome);

        //Buscar o tipo de veículo ativo por ID
         @Query("""
                        SELECT
                                v.id as id,
                                v.nome as nome,
                                v.capacidade as capacidade
                        FROM TipoVeiculoJpa v
                        WHERE
                                v.id=:id""")
        Optional<TipoVeiculoAtivoProjecao> buscarAtivoPorId(@Param("id") UUID id);


        //Buscar o tipo de veículo arquivado por ID
        @Query("""
                SELECT
                        v.id as id,
                        v.nome as nome,
                        v.capacidade as capacidade,
                        v.dataArquivado as dataArquivado
                FROM TipoVeiculoJpa v
                WHERE
                v.id = :id""")
        Optional<TipoVeiculoArquivadoProjecao> buscarArquivadoPorId(@Param("id") UUID id);

        //Listar tipos de veículos ativos com paginação e filtro por nome
        @Query("""
                SELECT
                        v.id as id,
                        v.nome as nome,
                        v.capacidade as capacidade
                FROM TipoVeiculoJpa v
                WHERE
                        v.nome LIKE CONCAT('%',:nome,'%')
                        AND v.dataArquivado IS NULL
                ORDER BY v.nome ASC""")
        Page<TipoVeiculoAtivoProjecao> listarAtivo(
                        @Param("nome") String nome,
                        Pageable pageable);

        //Listar tipos de veículos arquivados com paginação e filtro por nome
        @Query("""
                        SELECT
                                v.id as id,
                                v.nome as nome,
                                v.capacidade as capacidade,
                                v.dataArquivado as dataArquivado
                        FROM TipoVeiculoJpa v
                        WHERE
                                v.nome LIKE CONCAT('%',:nome,'%')
                                AND v.dataArquivado IS NOT NULL""")
        Page<TipoVeiculoArquivadoProjecao> listarArquivado(
                        @Param("nome") String nome,
                        Pageable pageable);



 }
