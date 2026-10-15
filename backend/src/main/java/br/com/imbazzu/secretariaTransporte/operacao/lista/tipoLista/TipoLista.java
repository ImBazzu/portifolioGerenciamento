//package br.com.imbazzu.secretariaTransporte.operacao.lista.tipoLista;
//
//import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.EntidadeDuplicadaException;
//import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.EntidadeNaoEncontradoException;
//import br.com.imbazzu.secretariaTransporte.trajeto.core.destino.domain.Destino;
//import br.com.imbazzu.secretariaTransporte.operacao.lista.ListaDoDia;
//import jakarta.persistence.*;
//import lombok.*;
//import org.hibernate.annotations.Filter;
//import org.hibernate.annotations.FilterDef;
//import org.hibernate.annotations.ParamDef;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.UUID;
//
//@Entity
//@Table(name = "listas", uniqueConstraints = {@UniqueConstraint(name = "uk_lista_nome", columnNames = "nome")})
//@Getter
//@NoArgsConstructor
//@FilterDef(
//        name = "ativoFilter",
//        parameters = @ParamDef(name = "ativoParam", type = Boolean.class)
//)
//@Filter(name = "ativoFilter", condition = "ativo = :ativoParam")
//public class TipoLista {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.UUID)
//    @Column(name = "id", columnDefinition = "BINARY(16)")
//    @Setter(AccessLevel.PRIVATE)
//    private UUID id;
//
//    @Column(name = "nome", columnDefinition = "VARCHAR(100)", nullable = false, unique = true)
//    private String nome;
//
//    @Column(name = "descricao", columnDefinition = "VARCHAR(255)")
//    private String descricao;
//
//    @OneToMany(mappedBy = "tipoLista")
//    @Setter(AccessLevel.PRIVATE)
//    private List<ListaDoDia> listasDoDia = new ArrayList<>();
//
//    @ManyToMany
//    @JoinTable(
//            name = "tipo_lista_destinos_permitidos",
//            joinColumns = @JoinColumn(name = "tipo_lista_id", columnDefinition = "BINARY(16)"),
//            inverseJoinColumns = @JoinColumn(name = "destino_id", columnDefinition = "BINARY(16)")
//    )
//    @Setter(AccessLevel.PRIVATE)
//    private List<Destino> destinosPermitidos = new ArrayList<>();
//
//    @Column(name = "ativo", nullable = false)
//    @Setter
//    private boolean ativo = true;
//
//
//    public TipoLista(String nome, String descricao) {
//        setNome(nome);
//        setDescricao(descricao);
//    }
//
//    public void setNome(String nome) {
//        this.nome = nome.toUpperCase();
//    }
//
//    public void setDescricao(String descricao) {
//        this.descricao =descricao == null?null: descricao.toUpperCase();
//    }
//
//    public void atualizar(String nome, String descricao) {
//        setNome(nome);
//        setDescricao(descricao);
//    }
//
//    public void addListaDoDia(ListaDoDia listaDoDia) {
//        listasDoDia.add(listaDoDia);
//        listaDoDia.setTipoLista(this);
//    }
//
//    public void removeListaDoDia(ListaDoDia listaDoDia) {
//        listasDoDia.remove(listaDoDia);
//        listaDoDia.setTipoLista(null);
//    }
//
//    public void addDestino(Destino destino) {
//        if (destinosPermitidos.contains(destino)) {
//            throw new EntidadeDuplicadaException("Destino já cadastrado");
//        }
//        destinosPermitidos.add(destino);
//    }
//
//    public void removeDestino(Destino destino) {
//        if (!destinosPermitidos.remove(destino)) {
//            throw new EntidadeNaoEncontradoException("Destino não cadastrado");
//        }
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (!(o instanceof TipoLista other)) return false;
//        return id != null && id.equals(other.getId());
//    }
//
//    @Override
//    public int hashCode() {
//        return getClass().hashCode();
//    }
//}