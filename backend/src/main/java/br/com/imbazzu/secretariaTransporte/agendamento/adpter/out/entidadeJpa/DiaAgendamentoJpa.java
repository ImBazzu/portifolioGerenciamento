package br.com.imbazzu.secretariaTransporte.agendamento.adpter.out.entidadeJpa;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "dias_agendamento")
@Getter
@Setter
@NoArgsConstructor
public class DiaAgendamentoJpa {

    @Id
    @Column(name = "data", nullable = false, updatable = false)
    private LocalDate data;

    @Column(name = "aberto_em", nullable = false)
    private LocalDateTime abertoEm;
}
