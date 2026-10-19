package br.com.imbazzu.secretariaTransporte.lista.adapter.out.entidadeJpa;
//package br.com.imbazzu.secretariaTransporte.operacao.regraSeparacao;
//
//import br.com.imbazzu.secretariaTransporte.trajeto.adapters.out.banco.cidade.CidadeJpa;
//import br.com.imbazzu.secretariaTransporte.trajeto.core.cidade.domain.Cidade;
//import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.DadosInvalidosException;
//import br.com.imbazzu.secretariaTransporte.compartilhados.util.DurationToMinutesConverter;
//import jakarta.persistence.*;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//
//import java.time.Duration;
//import java.time.LocalTime;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Set;
//import java.util.UUID;
//
///**
// * Configuração global do sistema para geração automática de viagens.
// * Singleton — existe exatamente uma instância no banco (id = 1).
// */
//@Entity
//@Table(name = "regra_viagem")
//@Getter
//@NoArgsConstructor
//public class RegraViagem {
//
//    @Id @GeneratedValue(strategy = GenerationType.UUID)
//    @Column(name="id",columnDefinition = "BINARY(16)", updatable = false,nullable = false)
//    private UUID id;
//
//    @Column(name = "capacidade_maxima",columnDefinition = "INT",nullable = false)
//    private int capacidadeMaxima;
//
//    @Convert(converter = DurationToMinutesConverter.class)
//    @Column(name = "tempo_tolerancia", nullable = false)
//    private Duration tempoTolerancia;
//
//    @OneToMany(mappedBy = "regraViagem", cascade = CascadeType.ALL, orphanRemoval = true)
//    private final List<ConjuntoCidades> conjuntos = new ArrayList<>();
//
//    // -------------------------------------------------------------------------
//    // Construtor
//    // -------------------------------------------------------------------------
//
//    public RegraViagem(int capacidadeMaxima, Duration tempo_tolerancia) {
//        setCapacidadeMaxima(capacidadeMaxima);
//        setTempoTolerancia(tempo_tolerancia);
//    }
//
//    // -------------------------------------------------------------------------
//    // Setters com validação
//    // -------------------------------------------------------------------------
//
//    public void setCapacidadeMaxima(int capacidadeMaxima) {
//        if (capacidadeMaxima < 1) {
//            throw new DadosInvalidosException("Capacidade máxima deve ser maior que zero");
//        }
//        this.capacidadeMaxima = capacidadeMaxima;
//    }
//
//    public void setTempoTolerancia(Duration tempoTolerancia) {
//        if (tempoTolerancia.isNegative()) {
//            throw new DadosInvalidosException("Tolerância não pode ser negativa");
//        }
//        this.tempoTolerancia = tempoTolerancia;
//    }
//
//    public boolean horariosCompativeis(LocalTime horaA, LocalTime horaB) {
//        return Duration.between(horaA, horaB).abs()
//                .compareTo(tempoTolerancia) <= 0;
//    }
//
//    // -------------------------------------------------------------------------
//    // Gerenciamento de conjuntos
//    // -------------------------------------------------------------------------
//
//    public ConjuntoCidades adicionarConjunto(String nome, Set<CidadeJpa> cidades) {
//        var conjunto = new ConjuntoCidades(this, nome, cidades);
//        this.conjuntos.add(conjunto);
//        return conjunto;
//    }
//
//    public void removerConjunto(ConjuntoCidades conjunto) {
//        if (!this.conjuntos.contains(conjunto)) {
//            throw new DadosInvalidosException("Conjunto não pertence a esta regra");
//        }
//        this.conjuntos.remove(conjunto);
//    }
//
//    // -------------------------------------------------------------------------
//    // Regras de negócio
//    // -------------------------------------------------------------------------
//
//    /**
//     * Verifica se duas cidades podem compartilhar o mesmo carro.
//     * São compatíveis se forem iguais ou se existir ao menos um conjunto que contenha ambas.
//     */
//    public boolean cidadesSaoCompativeis(Cidade cidadeA, Cidade cidadeB) {
//        if (cidadeA.equals(cidadeB)) return true;
//        return conjuntos.stream()
//                .anyMatch(c -> c.contemCidade(cidadeA) && c.contemCidade(cidadeB));
//    }
//}
