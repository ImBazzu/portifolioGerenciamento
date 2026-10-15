// package br.com.imbazzu.secretariaTransporte.operacao.passageiro;

// import
// br.com.imbazzu.secretariaTransporte.trajeto.core.destino.domain.Destino;
// import
// br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.DadosInvalidosException;
// import
// br.com.imbazzu.secretariaTransporte.pessoa.core.pessoaCondicao.domain.PessoaCondicao;
// import br.com.imbazzu.secretariaTransporte.operacao.lista.ListaDoDia;
// import br.com.imbazzu.secretariaTransporte.pessoa.core.pessoa.domain.Pessoa;
// import br.com.imbazzu.secretariaTransporte.operacao.viagem.Viagem;
// import jakarta.persistence.*;
// import lombok.*;

// import java.time.LocalTime;
// import java.util.UUID;

// @Entity
// @Table(name = "passageiros", uniqueConstraints = @UniqueConstraint(name =
// "uk_passageiro_lista_pessoa", columnNames = {
// "lista_id", "pessoa_id" }))
// @Getter
// @AllArgsConstructor
// @NoArgsConstructor
// public class Passageiro {

// @Id
// @GeneratedValue(strategy = GenerationType.UUID)
// @Column(name = "id", columnDefinition = "BINARY(16)", updatable = false,
// nullable = false)
// private UUID id;

// @ManyToOne
// @JoinColumn(name = "lista_id", columnDefinition = "BINARY(16)", nullable =
// false)
// @Setter
// private ListaDoDia listaDoDia;

// @ManyToOne
// @JoinColumn(name = "pessoa_id", columnDefinition = "BINARY(16)", nullable =
// false)
// @Setter
// private Pessoa pessoa;

// @ManyToOne
// @JoinColumn(name = "viagem_id", columnDefinition = "BINARY(16)")
// private Viagem viagem;

// @ManyToOne
// @JoinColumn(name = "destino_id", columnDefinition = "BINARY(16)", nullable =
// false)
// @Setter
// private Destino destino;

// @ManyToOne
// @JoinColumn(name = "justificativa_id", columnDefinition = "BINARY(16)",
// nullable = false)
// private PessoaCondicao pessoaCondicao;

// @Column(name = "acompanhantes", columnDefinition = "INT", nullable = false)
// private int acompanhantes;

// @Column(name = "hora_chegada", columnDefinition = "TIME", nullable = false)
// @Setter
// private LocalTime horaChegada;

// @Column(name = "buscar", columnDefinition = "BIT(1)", nullable = false)
// @Setter
// private boolean buscar;

// @Enumerated(EnumType.STRING)
// @Column(name = "status", nullable = false, columnDefinition = "VARCHAR(20)")
// @Setter
// private StatusPassageiro status;

// public Passageiro(ListaDoDia listaDoDia, Pessoa pessoa, Destino destino, int
// acompanhante,
// LocalTime horaChegada, boolean buscar) {
// setListaDoDia(listaDoDia);
// setPessoa(pessoa);
// setDestino(destino);
// setAcompanhantes(acompanhante);
// setHoraChegada(horaChegada);
// setBuscar(buscar);
// setStatus(StatusPassageiro.EM_ESPERA);
// }

// public LocalTime calcularHoraSaida() {
// return getHoraChegada().minus(getDestino().getCidade().getTempoViagem());
// }

// public int quantidadeTotalPassageiros() {
// return getAcompanhantes() + 1;
// }

// public String getEndereco() {
// if (this.buscar) {
// if (getPessoa().getEndereco().isEmpty()) {
// throw new DadosInvalidosException("Endereço não cadastrado");
// }
// ;
// }

// return this.buscar ? getPessoa().getEndereco() : "Vem até a Secretaria";
// }

// public void setAcompanhantes(int acompanhantes) {
// if (acompanhantes < 0) {
// throw new DadosInvalidosException("Acompanhantes não pode ser negativo");
// }
// this.acompanhantes = acompanhantes;
// }

// public void setViagem(Viagem viagem) {
// this.viagem = viagem;
// setStatus(StatusPassageiro.ALOCADO);
// }

// }
