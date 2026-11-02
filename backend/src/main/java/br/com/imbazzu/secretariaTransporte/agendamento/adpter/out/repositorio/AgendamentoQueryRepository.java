package br.com.imbazzu.secretariaTransporte.agendamento.adpter.out.repositorio;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import br.com.imbazzu.secretariaTransporte.agendamento.adpter.out.entidadeJpa.AgendamentoJpa;
import br.com.imbazzu.secretariaTransporte.agendamento.adpter.out.repositorio.projecoes.AgendamentoProjecao;

public interface AgendamentoQueryRepository extends Repository<AgendamentoJpa, UUID> {

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
                   a.idVeiculoAberto AS idVeiculoAberto
            FROM AgendamentoJpa a
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
                   a.idVeiculoAberto AS idVeiculoAberto
            FROM AgendamentoJpa a
            WHERE a.data = :data
            ORDER BY a.horarioChegada ASC
            """)
    List<AgendamentoProjecao> listarProjecoesPorData(@Param("data") LocalDate data);
}
