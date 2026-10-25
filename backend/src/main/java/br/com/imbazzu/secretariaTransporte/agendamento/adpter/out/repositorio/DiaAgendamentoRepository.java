package br.com.imbazzu.secretariaTransporte.agendamento.adpter.out.repositorio;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.imbazzu.secretariaTransporte.agendamento.adpter.out.entidadeJpa.DiaAgendamentoJpa;

public interface DiaAgendamentoRepository extends JpaRepository<DiaAgendamentoJpa, LocalDate> {
}
