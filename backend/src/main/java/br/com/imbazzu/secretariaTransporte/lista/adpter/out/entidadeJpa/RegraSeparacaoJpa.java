package br.com.imbazzu.secretariaTransporte.lista.adpter.out.entidadeJpa;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "regras_separacao")
@NoArgsConstructor
@Getter
@Setter
public class RegraSeparacaoJpa {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lista_id", nullable = false)
    private TipoListaJpa lista;

    @Column(name = "tipo_veiculo_id", nullable = false)
    private UUID idTipoVeiculo;

    @Column(name = "tempo_tolerancia_segundos", nullable = false)
    private long tempoToleranciaSegundos;

    @Column(name = "tempo_tolerancia_nanos", nullable = false)
    private int tempoToleranciaNanos;
}
