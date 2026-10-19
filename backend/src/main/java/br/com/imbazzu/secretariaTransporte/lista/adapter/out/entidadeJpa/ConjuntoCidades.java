package br.com.imbazzu.secretariaTransporte.lista.adapter.out.entidadeJpa;
//package br.com.imbazzu.secretariaTransporte.operacao.regraSeparacao;
//
//import br.com.imbazzu.secretariaTransporte.trajeto.adapters.out.banco.cidade.CidadeJpa;
//import br.com.imbazzu.secretariaTransporte.trajeto.core.cidade.domain.Cidade;
//import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.DadosInvalidosException;
//import jakarta.persistence.*;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//
//import java.util.HashSet;
//import java.util.Set;
//import java.util.UUID;
//
///**
// * Agrupa cidades que podem compartilhar um mesmo carro.
// * Pertence a {@link RegraViagem}. Relação com {@link Cidade} é unidirecional many-to-many.
// */
//@Entity
//@Table(name = "conjuntos_cidades")
//@Getter
//@NoArgsConstructor
//public class ConjuntoCidades {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.UUID)
//    private UUID id;
//
//    @ManyToOne(optional = false)
//    @JoinColumn(name = "regra_viagem_id", columnDefinition = "BINARY(16)",nullable = false, updatable = false)
//    private RegraViagem regraViagem;
//
//    @Column(name="nome", columnDefinition = "VARCHAR(100)",nullable = false)
//    private String nome;
//
//    @ManyToMany
//    @JoinTable(
//            name = "conjunto_cidade",
//            joinColumns = @JoinColumn(name = "conjunto_id", columnDefinition = "BINARY(16)"),
//            inverseJoinColumns = @JoinColumn(name = "cidade_id", columnDefinition = "BINARY(16)")
//    )
//    private final Set<CidadeJpa> cidades = new HashSet<>();
//
//    // -------------------------------------------------------------------------
//    // Construtor private — somente RegraViagem instancia
//    // -------------------------------------------------------------------------
//
//    ConjuntoCidades(RegraViagem regraViagem, String nome, Set<CidadeJpa> cidades) {
//        this.regraViagem = regraViagem;
//        setNome(nome);
//        if (cidades != null) {
//            this.cidades.addAll(cidades);
//        }
//    }
//
//    // -------------------------------------------------------------------------
//    // Setter com validação
//    // -------------------------------------------------------------------------
//
//    public void setNome(String nome) {
//        if (nome == null || nome.isBlank()) {
//            throw new DadosInvalidosException("Nome do conjunto não pode ser vazio");
//        }
//        this.nome = nome.trim().toUpperCase();
//    }
//
//    // -------------------------------------------------------------------------
//    // Gerenciamento de cidades
//    // -------------------------------------------------------------------------
//
//    public void adicionarCidade(CidadeJpa cidade) {
//        this.cidades.add(cidade);
//    }
//
//    public void removerCidade(Cidade cidade) {
//        if (!this.cidades.contains(cidade)) {
//            throw new DadosInvalidosException("Cidade não presente no conjunto");
//        }
//        this.cidades.remove(cidade);
//    }
//
//    public boolean contemCidade(Cidade cidade) {
//        return this.cidades.contains(cidade);
//    }
//}
