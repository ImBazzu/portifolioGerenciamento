package br.com.imbazzu.secretariaTransporte.cadastrais.adapter.out.entidadesJpa;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;


/**
 * Classe representa a tabela pessoas
 */
@Entity
@Table(name = "pessoas", uniqueConstraints = @UniqueConstraint(name = "uk_pessoa_cpf", columnNames = { "cpf" }))
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PessoaJpa {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "nome", nullable = false, length = 60)
    private String nome;

    @Column(name = "cpf",nullable = false, unique = true, length = 11)
    private String cpf;
    @Column(name = "pessoaCondicaoId", nullable = false)
    private UUID pessoaCondicaoId;

    @Column(name = "telefone",nullable = false, length = 11)
    private String telefone;

    @Column(name = "endereco",length = 250)
    private String endereco;

    @Column(name = "observacao", length = 250)
    private String observacao;


    @Column(name = "arquivado", nullable = false)
    private boolean arquivado;



}
