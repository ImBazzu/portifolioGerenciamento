package br.com.imbazzu.secretariaTransporte.agendamento.adpter.out.entidadeJpa;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Duration;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "agendamentos")
@Getter
@Setter
@NoArgsConstructor
public class AgendamentoJpa {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "data", nullable = false)
    private LocalDate data;

    @Column(name = "pessoa_id", nullable = false)
    private UUID idPessoa;

    @Column(name = "lista_id", nullable = false)
    private UUID idLista;

    @Column(name = "regra_cadastro_id", nullable = false)
    private UUID idRegraCadastro;

    @Column(name = "tipo_veiculo_id", nullable = false)
    private UUID idTipoVeiculo;

    @Column(name = "destino_id", nullable = false)
    private UUID idDestino;

    @Column(name = "tempo_viagem_segundos", nullable = false)
    private long tempoViagemSegundos;

    @Column(name = "tempo_viagem_nanos", nullable = false)
    private int tempoViagemNanos;

    @Column(name = "tolerancia_segundos", nullable = false)
    private long toleranciaSegundos;

    @Column(name = "tolerancia_nanos", nullable = false)
    private int toleranciaNanos;

    @Column(name = "hora_chegada", nullable = false)
    private LocalTime horarioChegada;

    @Column(name = "buscar_na_residencia", nullable = false)
    private boolean buscarNaResidencia;

    @Column(name = "acompanhantes", nullable = false)
    private int acompanhantes;

    @Column(name = "veiculo_agendamento_id")
    private UUID idVeiculoAberto;

    public Duration getTempoViagem() {
        return Duration.ofSeconds(tempoViagemSegundos, tempoViagemNanos);
    }

    public void setTempoViagem(Duration tempoViagem) {
        this.tempoViagemSegundos = tempoViagem.getSeconds();
        this.tempoViagemNanos = tempoViagem.getNano();
    }

    public Duration getTolerancia() {
        return Duration.ofSeconds(toleranciaSegundos, toleranciaNanos);
    }

    public void setTolerancia(Duration tolerancia) {
        this.toleranciaSegundos = tolerancia.getSeconds();
        this.toleranciaNanos = tolerancia.getNano();
    }
}
