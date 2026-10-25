package br.com.imbazzu.secretariaTransporte.agendamento.adpter.out.repositorio;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.imbazzu.secretariaTransporte.agendamento.adpter.out.entidadeJpa.AgendamentoJpa;

public interface AgendamentoRepository extends JpaRepository<AgendamentoJpa, UUID> {

    boolean existsByDataAndIdPessoa(LocalDate data, UUID idPessoa);

    List<AgendamentoJpa> findByDataOrderByHorarioChegadaAsc(LocalDate data);
}
