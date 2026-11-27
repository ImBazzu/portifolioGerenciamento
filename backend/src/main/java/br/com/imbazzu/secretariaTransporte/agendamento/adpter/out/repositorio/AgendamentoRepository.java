package br.com.imbazzu.secretariaTransporte.agendamento.adpter.out.repositorio;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.imbazzu.secretariaTransporte.agendamento.adpter.out.entidadeJpa.AgendamentoJpa;

public interface AgendamentoRepository extends JpaRepository<AgendamentoJpa, UUID> {

    boolean existsByDataAndIdPessoa(LocalDate data, UUID idPessoa);

    boolean existsByDataAndIdPessoaAndCanceladoEmIsNull(LocalDate data, UUID idPessoa);

    List<AgendamentoJpa> findByDataOrderByHorarioChegadaAsc(LocalDate data);

    @Modifying
    @Query("""
            UPDATE AgendamentoJpa a
            SET a.canceladoEm = :canceladoEm,
                a.motivoCancelamento = :motivo,
                a.canceladoPorId = :autorId,
                a.idVeiculoAberto = null
            WHERE a.id = :id AND a.canceladoEm IS NULL
            """)
    int cancelar(
            @Param("id") UUID id,
            @Param("canceladoEm") java.time.LocalDateTime canceladoEm,
            @Param("motivo") String motivo,
            @Param("autorId") UUID autorId);
}
