package br.com.imbazzu.secretariaTransporte.especificacao.adpter.out.entidadeJpa;

import java.time.LocalDate;
import java.util.UUID;

import br.com.imbazzu.secretariaTransporte.pessoa.core.domain.EnumPrioridade;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "especificacoes", uniqueConstraints = {
        @UniqueConstraint(name = "uk_especificacao_nome", columnNames = "nome") })
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EspecificacaoJpa {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(name = "prioridade", nullable = false, length = 20)
    private EnumPrioridade prioridade;

    @Column(name = "observacao", length = 249)
    private String observacao;

    @Column(name = "data_arquivado")
    private LocalDate dataArquivado;
}
