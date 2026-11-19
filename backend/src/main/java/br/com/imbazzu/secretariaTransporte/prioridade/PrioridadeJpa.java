package br.com.imbazzu.secretariaTransporte.prioridade;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "prioridades")
@Getter
@Setter
@NoArgsConstructor
public class PrioridadeJpa {
    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false, length = 100, unique = true)
    private String nome;

    @Column(name = "buscar_na_residencia", nullable = false)
    private boolean buscarNaResidencia;

    @Column(name = "quantidade_acompanhantes", nullable = false)
    private int quantidadeAcompanhantes;

    @Column(name = "data_arquivado")
    private LocalDate dataArquivado;
}
