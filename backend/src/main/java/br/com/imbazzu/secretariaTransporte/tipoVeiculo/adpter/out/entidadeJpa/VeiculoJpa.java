package br.com.imbazzu.secretariaTransporte.tipoVeiculo.adpter.out.entidadeJpa;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Representa uma viagem realizada por um motorista com um conjunto de
 * passageiros.
 * A data da viagem é derivada da {@link ListaDoDia}
 * à qual os passageiros pertencem — não é armazenada aqui.
 */
@Entity
@Table(name = "viagens")
@Getter
@Setter
@NoArgsConstructor
public class VeiculoJpa {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "idTipoVeiculo", nullable = false)
    private UUID idTipoVeiculo;

    @Column(name = "dataSaida", nullable = false)
    private LocalDate dataSaida;

    @Column(name = "horaSaida", nullable = false)
    private LocalTime horaSaida;

    @Column(name = "qtdPassageiro", nullable = false)
    private Integer qtdPassageiros;

}