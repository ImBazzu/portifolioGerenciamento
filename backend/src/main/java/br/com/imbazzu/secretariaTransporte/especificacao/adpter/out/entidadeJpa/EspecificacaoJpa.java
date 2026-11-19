package br.com.imbazzu.secretariaTransporte.especificacao.adpter.out.entidadeJpa;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

    @Column(name = "prioridade_id", nullable = false)
    private UUID prioridade;

    @Column(name = "observacao", length = 249)
    private String observacao;

    @Column(name = "data_arquivado")
    private LocalDate dataArquivado;
}
