package br.com.imbazzu.secretariaTransporte.pessoa.adpter.out.entidadeJpa;

import java.time.LocalDate;
import java.util.UUID;

import br.com.imbazzu.secretariaTransporte.pessoa.core.domain.EnumPrioridade;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "pessoas")
@Getter
@Setter
@NoArgsConstructor
public class PessoaJpa {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "nome", nullable = false, length = 150)
    private String nome;

    @Column(name = "observacao", length = 249)
    private String observacao;

    @Enumerated(EnumType.STRING)
    @Column(name = "prioridade", nullable = false, length = 20)
    private EnumPrioridade prioridade;

    @Column(name = "data_arquivado")
    private LocalDate dataArquivado;

    @Column(name = "endereco", nullable = false, length = 250)
    private String endereco;

    @Column(name = "cpf", nullable = false, length = 11)
    private String cpf;

    @Column(name = "telefone", nullable = false, length = 11)
    private String telefone;
}
