package br.com.imbazzu.secretariaTransporte.agendamento.adpter.out.repositorio;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.imbazzu.secretariaTransporte.agendamento.adpter.out.entidadeJpa.VeiculoAgendaJpa;

public interface VeiculoAgendaRepository extends JpaRepository<VeiculoAgendaJpa, UUID> {

    List<VeiculoAgendaJpa> findByDataOrderById(LocalDate data);
}
