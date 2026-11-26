package br.com.imbazzu.secretariaTransporte.pessoa.adpter.out.entidadeJpa;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import br.com.imbazzu.secretariaTransporte.especificacao.adpter.out.entidadeJpa.EspecificacaoJpa;
import br.com.imbazzu.secretariaTransporte.seguranca.usuario.adapter.out.persistencia.UsuarioJpaEntity;
import jakarta.persistence.FetchType;

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

    @ManyToOne
    @JoinColumn(name = "especificacao_id", nullable = false, foreignKey = @ForeignKey(name = "fk_pessoa_especificacao"))
    private EspecificacaoJpa especificacao;

    @Column(name = "buscar_na_residencia", nullable = false)
    private boolean buscarNaResidencia;

    @Column(name = "quantidade_acompanhantes", nullable = false)
    private int quantidadeAcompanhantes;

    @Column(name = "validada", nullable = false)
    private boolean validada;

    @Column(name = "data_arquivado")
    private LocalDate dataArquivado;

    @Column(name = "endereco", length = 250)
    private String endereco;

    @Column(name = "cpf", length = 11)
    private String cpf;

    @Column(name = "telefone", nullable = false, length = 11)
    private String telefone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "autor_id", updatable = false, foreignKey = @ForeignKey(name = "fk_pessoa_autor"))
    private UsuarioJpaEntity autor;
}
