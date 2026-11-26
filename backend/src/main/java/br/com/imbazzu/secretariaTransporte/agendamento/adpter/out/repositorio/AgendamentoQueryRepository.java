package br.com.imbazzu.secretariaTransporte.agendamento.adpter.out.repositorio;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.imbazzu.secretariaTransporte.agendamento.adpter.out.entidadeJpa.AgendamentoJpa;
import br.com.imbazzu.secretariaTransporte.agendamento.adpter.out.repositorio.projecoes.AgendamentoProjecao;
import br.com.imbazzu.secretariaTransporte.agendamento.adpter.out.repositorio.projecoes.CidadesCompativeisProjecao;
import br.com.imbazzu.secretariaTransporte.agendamento.adpter.out.repositorio.projecoes.DestinoPorCidadeProjecao;
import br.com.imbazzu.secretariaTransporte.agendamento.adpter.out.repositorio.projecoes.HistoricoViagemProjecao;

public interface AgendamentoQueryRepository extends JpaRepository<AgendamentoJpa, UUID> {

    boolean existsByDataAndIdPessoa(LocalDate data, UUID idPessoa);

    @Query("""
            SELECT a.id AS id, a.data AS data, a.idPessoa AS idPessoa,
                   a.idLista AS idLista, a.idRegraCadastro AS idRegraCadastro,
                   a.idTipoVeiculo AS idTipoVeiculo, a.idDestino AS idDestino,
                   a.tempoViagemSegundos AS tempoViagemSegundos,
                   a.tempoViagemNanos AS tempoViagemNanos,
                   a.toleranciaSegundos AS toleranciaSegundos,
                   a.toleranciaNanos AS toleranciaNanos,
                   a.horarioChegada AS horarioChegada,
                   a.buscarNaResidencia AS buscarNaResidencia,
                   a.acompanhantes AS acompanhantes,
                   a.idVeiculoAberto AS idVeiculoAberto,
                   autor.id AS autorId, autor.nome AS autorNome
            FROM AgendamentoJpa a
            LEFT JOIN a.autor autor
            WHERE a.id = :id
            """)
    Optional<AgendamentoProjecao> buscarProjecaoPorId(@Param("id") UUID id);

    @Query("""
            SELECT a.id AS id, a.data AS data, a.idPessoa AS idPessoa,
                   a.idLista AS idLista, a.idRegraCadastro AS idRegraCadastro,
                   a.idTipoVeiculo AS idTipoVeiculo, a.idDestino AS idDestino,
                   a.tempoViagemSegundos AS tempoViagemSegundos,
                   a.tempoViagemNanos AS tempoViagemNanos,
                   a.toleranciaSegundos AS toleranciaSegundos,
                   a.toleranciaNanos AS toleranciaNanos,
                   a.horarioChegada AS horarioChegada,
                   a.buscarNaResidencia AS buscarNaResidencia,
                   a.acompanhantes AS acompanhantes,
                   a.idVeiculoAberto AS idVeiculoAberto,
                   autor.id AS autorId, autor.nome AS autorNome
            FROM AgendamentoJpa a
            LEFT JOIN a.autor autor
            WHERE a.data = :data
            ORDER BY a.horarioChegada ASC
            """)
    List<AgendamentoProjecao> listarProjecoesPorData(@Param("data") LocalDate data);

        @Query("""
                SELECT DISTINCT
                        d.cidadeId AS cidadeId,
                        a.idDestino AS destinoId
                FROM AgendamentoJpa a
                JOIN DestinoJpa d ON d.id = a.idDestino
                WHERE a.data = :data""")
     List<DestinoPorCidadeProjecao> buscarDestinosPorCidadeId(@Param("data") LocalDate data);

    @Query("""
            SELECT DISTINCT d1.cidadeId AS cidadeId, d2.cidadeId AS cidadeCompativelId
            FROM AgendamentoJpa a1
            JOIN AgendamentoJpa a2 ON a2.idVeiculoAberto = a1.idVeiculoAberto AND a2.data = a1.data
            JOIN DestinoJpa d1 ON d1.id = a1.idDestino
            JOIN DestinoJpa d2 ON d2.id = a2.idDestino
            WHERE a1.data < :data AND d1.cidadeId <> d2.cidadeId
            """)
    List<CidadesCompativeisProjecao> buscarCidadesCompativeisAnteriores(@Param("data") LocalDate data);

    @Query("""
            SELECT 
                    a.id AS id,
                    a.data AS data,
                    a.horarioChegada AS horarioChegada,
                    a.tempoViagemSegundos AS tempoViagemSegundos,
                    a.tempoViagemNanos AS tempoViagemNanos,
                    d.nome AS destinoNome,
                    l.descricao AS listaDescricao,
                    tv.nome AS tipoVeiculoNome,
                    a.buscarNaResidencia AS buscarNaResidencia,
                    a.acompanhantes AS acompanhantes
            FROM AgendamentoJpa a
            JOIN DestinoJpa d ON d.id = a.idDestino
            JOIN TipoListaJpa l ON l.id = a.idLista
            JOIN TipoVeiculoJpa tv ON tv.id = a.idTipoVeiculo
            WHERE a.idPessoa = :idPessoa
            ORDER BY a.data DESC
            """)
    Page<HistoricoViagemProjecao> buscarHistoricoViagensPorPessoa(
            @Param("idPessoa") UUID idPessoa,
            Pageable pageable);

    @Query("""
            SELECT 
                    a.id AS id,
                    a.data AS data,
                    a.horarioChegada AS horarioChegada,
                    a.tempoViagemSegundos AS tempoViagemSegundos,
                    a.tempoViagemNanos AS tempoViagemNanos,
                    d.nome AS destinoNome,
                    l.descricao AS listaDescricao,
                    tv.nome AS tipoVeiculoNome,
                    a.buscarNaResidencia AS buscarNaResidencia,
                    a.acompanhantes AS acompanhantes
            FROM AgendamentoJpa a
            JOIN DestinoJpa d ON d.id = a.idDestino
            JOIN TipoListaJpa l ON l.id = a.idLista
            JOIN TipoVeiculoJpa tv ON tv.id = a.idTipoVeiculo
            WHERE a.idPessoa = :idPessoa
            AND d.nome LIKE CONCAT('%', :destinoBusca, '%')
            ORDER BY a.data DESC
            """)
    Page<HistoricoViagemProjecao> buscarHistoricoViagensPorPessoaComBusca(
            @Param("idPessoa") UUID idPessoa,
            @Param("destinoBusca") String destinoBusca,
            Pageable pageable);
}
