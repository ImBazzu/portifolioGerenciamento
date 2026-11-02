package br.com.imbazzu.secretariaTransporte.agendamento.adpter.out.repositorio;

import java.time.LocalDate;

import org.springframework.data.repository.Repository;

import br.com.imbazzu.secretariaTransporte.agendamento.adpter.out.entidadeJpa.DiaAgendamentoJpa;

public interface DiaAgendamentoQueryRepository extends Repository<DiaAgendamentoJpa, LocalDate> {

    boolean existsById(LocalDate data);
}
