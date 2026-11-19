package br.com.imbazzu.secretariaTransporte.lista.adpter.out.entidadeJpa;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "regras_cadastro")
@NoArgsConstructor
@Getter
@Setter
public class RegraCadastroJpa {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lista_id", nullable = false)
    private TipoListaJpa lista;

    @ElementCollection
    @CollectionTable(name = "regra_cadastro_prioridades", joinColumns = @JoinColumn(name = "regra_cadastro_id"),
            foreignKey = @ForeignKey(name = "fk_regra_prioridade_cadastro"))
    @Column(name = "prioridade_id", nullable = false)
    private List<UUID> prioridades = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "regra_cadastro_veiculos", joinColumns = @JoinColumn(name = "regra_cadastro_id"),
            foreignKey = @ForeignKey(name = "fk_regra_veiculo_cadastro"))
    @Column(name = "veiculo_id", nullable = false)
    private List<UUID> idsVeiculos = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "regra_cadastro_cidades", joinColumns = @JoinColumn(name = "regra_cadastro_id"),
            foreignKey = @ForeignKey(name = "fk_regra_cidade_cadastro"))
    @Column(name = "cidade_id", nullable = false)
    private List<UUID> idsCidades = new ArrayList<>();

    @Column(name = "obrigacao_anexo", nullable = false)
    private boolean obrigacaoAnexo;

    @Column(name = "descricao", length = 249)
    private String descricao;

    @Column(name = "inicio_horario")
    private LocalTime inicioHorario;

    @Column(name = "final_horario")
    private LocalTime finalHorario;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "regra_cadastro_dias_semana", joinColumns = @JoinColumn(name = "regra_cadastro_id"),
            foreignKey = @ForeignKey(name = "fk_regra_dia_semana_cadastro"))
    @Column(name = "dia_semana", nullable = false, length = 10)
    private List<DayOfWeek> diasSemana = new ArrayList<>();

    @Column(name = "intervalo_repeticao_semanas", nullable = false)
    private Integer intervaloRepetacao;
}
