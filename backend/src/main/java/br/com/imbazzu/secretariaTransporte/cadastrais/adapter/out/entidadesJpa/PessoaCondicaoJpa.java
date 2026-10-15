package br.com.imbazzu.secretariaTransporte.cadastrais.adapter.out.entidadesJpa;

import br.com.imbazzu.secretariaTransporte.cadastrais.core.model.PessoaCondicao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "justificativas", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"nome"})
})
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PessoaCondicaoJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "nome",nullable = false, length = 60)
    private String nome;

    @Column(name = "arquivado",nullable = false)
    @Setter
    private boolean arquivado;


    public PessoaCondicaoJpa(PessoaCondicao pessoaCondicao) {
        this.id = pessoaCondicao.getId();
        this.nome = pessoaCondicao.getNome().valor();
        this.arquivado = pessoaCondicao.isArquivado();
    }
}
