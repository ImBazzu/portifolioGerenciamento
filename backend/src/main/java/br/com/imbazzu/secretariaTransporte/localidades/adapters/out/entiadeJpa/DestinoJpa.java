package br.com.imbazzu.secretariaTransporte.localidades.adapters.out.entiadeJpa;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Representa o local onde o passageiro irá
 */
@Entity
@Table(name = "destinos", uniqueConstraints = @UniqueConstraint(columnNames = { "nome",
        "cidade_id" }, name = "uk_destinos_nome_cidade"))
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DestinoJpa {
    /**
     * Identificador único
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    @Setter(AccessLevel.PRIVATE)
    private UUID id;

    /**
     * Nome do destino
     */
    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    /**
     * A cidade onde o destino se encontra
     */
    @Column(name = "cidade_id", nullable = false, updatable = false)
    private UUID cidadeId;

    @Column(name = "dataArquivado", nullable = false)
    private LocalDate dataArquivado;
}
