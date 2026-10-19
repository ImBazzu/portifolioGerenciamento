package br.com.imbazzu.secretariaTransporte.lista.adapter.out.entidadeJpa;
//package br.com.imbazzu.secretariaTransporte.operacao.lista;
//
//import br.com.imbazzu.secretariaTransporte.trajeto.core.cidade.domain.Cidade;
//import br.com.imbazzu.secretariaTransporte.trajeto.core.destino.domain.Destino;
//import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.DadosInvalidosException;
//import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.EntidadeDuplicadaException;
//import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.EntidadeNaoEncontradoException;
//import br.com.imbazzu.secretariaTransporte.operacao.lista.tipoLista.TipoLista;
//import br.com.imbazzu.secretariaTransporte.operacao.passageiro.Passageiro;
//import br.com.imbazzu.secretariaTransporte.pessoa.core.pessoa.domain.Pessoa;
//import jakarta.persistence.*;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//import java.time.LocalDate;
//import java.time.LocalTime;
//import java.util.*;
//
///**
// * Entidade referente a tabela da tipoLista
// */
//@Entity
//@Table(name="listasDoDia")
//@Getter
//@NoArgsConstructor
//public class ListaDoDia {
//
//    /**
//     * Identificador único
//     */
//    @Id @GeneratedValue(strategy = GenerationType.UUID)
//    @Column(name="id",unique = true, nullable = false, columnDefinition = "BINARY(16)")
//    private UUID id;
//
//    @ManyToOne
//    @JoinColumn(name = "lista_id", columnDefinition = "BINARY(16)", nullable = false)
//    @Setter
//    private TipoLista tipoLista;
//
//    @Column(name = "data",columnDefinition = "DATE", nullable = false)
//    @Setter
//    private LocalDate data;
//
//    @Column(name = "quantidade", nullable = false, columnDefinition = "INT")
//    private int quantidade=0;
//
//    //Passageiros que foram cadastrados na tipoLista
//    @OneToMany(mappedBy = "listaDoDIa")
//    private final List<Passageiro> passageiros = new ArrayList<>();
//
//    public ListaDoDia(TipoLista tipoLista, LocalDate data) {
//        tipoLista.addListaDoDia(this);
//        setData(data);
//    }
//
//    //Adiciona um passageiro
//    public Passageiro adicionarPassageiro(Pessoa pessoa, Destino destino, int acompanhante,
//                                    LocalTime horaChegada, boolean buscar) {
//        var passageiro = new Passageiro(this,pessoa,destino,acompanhante,horaChegada,buscar);
//        return adicionarPassageiro(passageiro);
//    }
//
//    public Passageiro adicionarPassageiro(Passageiro passageiro) {
//        if(this.passageiros.stream().anyMatch(p -> p.getPessoa().equals(passageiro.getPessoa()))) {
//            throw new EntidadeDuplicadaException("Pessoa ja cadastrada na tipoLista");
//        }
//        this.passageiros.add(passageiro);
//        passageiro.setListaDoDia(this);
//        addQuantidade(passageiro.getAcompanhantes()+1);
//        return passageiro;
//    }
//
//
//    //Remove um passageiro
//    public void removerPassageiro(Passageiro passageiro){
//        //OrphanRemove é true significa que ao ser retirado da tipoLista é apagado do banco de dados
//        if(!this.passageiros.contains(passageiro)){
//            throw new DadosInvalidosException("Passageiro não presente na tipoLista");
//        }
//        //Remove o passageiro
//        this.passageiros.remove(passageiro);
//        diminuirQuantidade(passageiro.getAcompanhantes()+1);
//        //Desvincula a tipoLista do passageiro
//        passageiro.setListaDoDia(null);
//    }
//
//    //Retorna uma tipoLista resumida de todos os destinos dos passageiros
//    public List<Cidade> getCidades() {
//        return this.passageiros.stream().map(
//                passageiro -> passageiro.getDestino().getCidade())
//                .distinct().sorted(Comparator.comparing(Cidade::getNome))
//                .toList();
//    }
//
//    public void addQuantidade(int quantidade) {
//        this.quantidade+=quantidade;
//    }
//
//    public void diminuirQuantidade(int quantidade) {
//        this.quantidade-=quantidade;
//    }
//
//    public Passageiro buscarPassageiro(UUID idPessoa){
//        return this.passageiros.stream().filter(p -> p.getPessoa().getId().equals(idPessoa)).findFirst()
//                .orElseThrow(()->new EntidadeNaoEncontradoException("Passageiro não encontrado"));
//    }
//
//
//}
