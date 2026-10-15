package br.com.imbazzu.secretariaTransporte.tipoVeiculo.adpter.out.entidadeJpa;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "tipo_veiculos", uniqueConstraints = {
        @UniqueConstraint(name = "uk_tipoveiculo_nome", columnNames = "nome") })
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TipoVeiculoJpa {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "nome", nullable = false, length = 20)
    private String nome;

    @Column(name = "capacidade", nullable = false)
    private int capacidade;

    @Column(name = "data_arquivado")
    private LocalDate dataArquivado;

}
