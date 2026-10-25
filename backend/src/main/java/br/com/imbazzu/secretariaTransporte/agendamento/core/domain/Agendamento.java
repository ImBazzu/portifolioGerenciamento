package br.com.imbazzu.secretariaTransporte.agendamento.core.domain;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;
import java.util.UUID;

import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.DadosInvalidosException;

public final class Agendamento {

    private final UUID id;
    private final LocalDate data;
    private final UUID idPessoa;
    private final UUID idLista;
    private final UUID idRegraCadastro;
    private final UUID idTipoVeiculo;
    private final UUID idDestino;
    private final Duration tempoViagem;
    private final Duration toleranciaSeparacao;
    private final LocalTime horarioChegada;
    private final boolean buscarNaResidencia;
    private final int acompanhantes;
    private UUID idVeiculoAberto;

    private Agendamento(UUID id, LocalDate data, UUID idPessoa, UUID idLista, UUID idRegraCadastro,
            UUID idTipoVeiculo, UUID idDestino, Duration tempoViagem, Duration toleranciaSeparacao,
            LocalTime horarioChegada,
            boolean buscarNaResidencia, int acompanhantes, UUID idVeiculoAberto) {
        this.id = Objects.requireNonNull(id, "ID do agendamento é obrigatório");
        this.data = Objects.requireNonNull(data, "Data do agendamento é obrigatória");
        this.idPessoa = Objects.requireNonNull(idPessoa, "Pessoa é obrigatória");
        this.idLista = Objects.requireNonNull(idLista, "Lista é obrigatória");
        this.idRegraCadastro = Objects.requireNonNull(idRegraCadastro, "Regra de cadastro é obrigatória");
        this.idTipoVeiculo = Objects.requireNonNull(idTipoVeiculo, "Tipo de veículo é obrigatório");
        this.idDestino = Objects.requireNonNull(idDestino, "Destino é obrigatório");
        this.tempoViagem = Objects.requireNonNull(tempoViagem, "Tempo de viagem é obrigatório");
        this.toleranciaSeparacao = Objects.requireNonNull(toleranciaSeparacao, "Tolerância é obrigatória");
        if (tempoViagem.isNegative() || toleranciaSeparacao.isNegative()) {
            throw new DadosInvalidosException("Tempo de viagem e tolerância não podem ser negativos");
        }
        this.horarioChegada = Objects.requireNonNull(horarioChegada, "Horário de chegada é obrigatório");
        if (acompanhantes < 0) {
            throw new DadosInvalidosException("A quantidade de acompanhantes não pode ser negativa");
        }
        this.buscarNaResidencia = buscarNaResidencia;
        this.acompanhantes = acompanhantes;
        this.idVeiculoAberto = idVeiculoAberto;
    }

    public static Agendamento criar(LocalDate data, UUID idPessoa, UUID idLista, UUID idRegraCadastro,
            UUID idTipoVeiculo, UUID idDestino, Duration tempoViagem, Duration toleranciaSeparacao,
            LocalTime horarioChegada,
            boolean buscarNaResidencia, int acompanhantes) {
        return new Agendamento(UUID.randomUUID(), data, idPessoa, idLista, idRegraCadastro,
                idTipoVeiculo, idDestino, tempoViagem, toleranciaSeparacao, horarioChegada,
                buscarNaResidencia, acompanhantes, null);
    }

    public static Agendamento recriar(UUID id, LocalDate data, UUID idPessoa, UUID idLista,
            UUID idRegraCadastro, UUID idTipoVeiculo, UUID idDestino, Duration tempoViagem,
            Duration toleranciaSeparacao, LocalTime horarioChegada,
            boolean buscarNaResidencia, int acompanhantes, UUID idVeiculoAberto) {
        return new Agendamento(id, data, idPessoa, idLista, idRegraCadastro,
                idTipoVeiculo, idDestino, tempoViagem, toleranciaSeparacao, horarioChegada,
                buscarNaResidencia, acompanhantes, idVeiculoAberto);
    }

    public void atribuirVeiculo(UUID idVeiculoAberto) {
        this.idVeiculoAberto = Objects.requireNonNull(idVeiculoAberto, "Veículo físico é obrigatório");
    }

    public void removerVeiculo() {
        this.idVeiculoAberto = null;
    }

    public int quantidadePessoas() {
        return acompanhantes + 1;
    }

    public UUID getId() { return id; }
    public LocalDate getData() { return data; }
    public UUID getIdPessoa() { return idPessoa; }
    public UUID getIdLista() { return idLista; }
    public UUID getIdRegraCadastro() { return idRegraCadastro; }
    public UUID getIdTipoVeiculo() { return idTipoVeiculo; }
    public UUID getIdDestino() { return idDestino; }
    public Duration getTempoViagem() { return tempoViagem; }
    public Duration getToleranciaSeparacao() { return toleranciaSeparacao; }
    public LocalTime getHorarioChegada() { return horarioChegada; }
    public boolean isBuscarNaResidencia() { return buscarNaResidencia; }
    public int getAcompanhantes() { return acompanhantes; }
    public UUID getIdVeiculoAberto() { return idVeiculoAberto; }
}
