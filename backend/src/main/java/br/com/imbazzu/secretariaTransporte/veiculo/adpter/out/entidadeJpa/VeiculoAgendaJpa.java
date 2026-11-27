package br.com.imbazzu.secretariaTransporte.veiculo.adpter.out.entidadeJpa;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import br.com.imbazzu.secretariaTransporte.veiculo.core.domain.FinalidadeAberturaVeiculo;

@Entity
@Table(name = "veiculos_agendamento")
@Getter
@Setter
@NoArgsConstructor
@DynamicUpdate
public class VeiculoAgendaJpa {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "data", nullable = false)
    private LocalDate data;

    @Column(name = "tipo_veiculo_id", nullable = false)
    private UUID idTipoVeiculo;

    @Column(name = "nome_tipo_veiculo", nullable = false, length = 100)
    private String nomeTipoVeiculo;

    @Column(name = "capacidade", nullable = false)
    private int capacidade;

    @Column(name = "identificacao", length = 80)
    private String identificacao;

    @Column(name = "hora_saida")
    private LocalTime horarioSaida;

    @Column(name = "hora_saida_calculada", nullable = false)
    private boolean horarioSaidaCalculado;

    @Column(name = "motorista_id")
    private UUID idMotorista;

    @Column(name = "saida_confirmada_em")
    private LocalDateTime saidaConfirmadaEm;

    @Column(name = "saida_confirmada_por_id")
    private UUID saidaConfirmadaPorId;

    @Column(name = "finalidade_abertura", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private FinalidadeAberturaVeiculo finalidadeAbertura;

    @Column(name = "lista_abertura_id")
    private UUID idListaAbertura;
}
