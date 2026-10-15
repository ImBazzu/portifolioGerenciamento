package br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.model;

import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.DadosInvalidosException;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.model.valueobject.NomeVeiculo;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public class TipoVeiculo {

    private UUID id;

    private NomeVeiculo nome;

    private int capacidade;

    private LocalDate dataArquivado;

    private TipoVeiculo(UUID id, NomeVeiculo nome, int capacidade, LocalDate dataArquivado) {
        Objects.requireNonNull(id, "Id é obrigatório");
        Objects.requireNonNull(nome, "Nome do veiculo é obrigatório");

        this.id = id;
        this.nome = nome;
        this.capacidade = capacidade;
        this.dataArquivado = dataArquivado;
    }

    public static TipoVeiculo criar(NomeVeiculo nome, int capacidade) {
        Objects.requireNonNull(nome, "Nome do veiculo é obrigatório");
        if (capacidade <= 0) {
            throw new DadosInvalidosException("Capacidade informada não pode ser igual ou menor que zero");
        }
        return new TipoVeiculo(UUID.randomUUID(), nome, capacidade, null);
    }

    public static TipoVeiculo recriar(UUID id, NomeVeiculo nome, int capacidade, LocalDate dataArquivado) {
        return new TipoVeiculo(id, nome, capacidade, dataArquivado);
    }

    public void editarVeiculo(NomeVeiculo nome, int capacidade) {
        alterarCapacidade(capacidade);
        alterarNome(nome);
    }

    public void alterarNome(NomeVeiculo nome) {
        Objects.requireNonNull(nome, "Nome do veiculo é obrigatório");
        this.nome = nome;
    }

    public void alterarCapacidade(int capacidade) {
        if (capacidade <= 0) {
            throw new DadosInvalidosException("Capacidade informada não pode ser igual ou menor que zero");
        }
        this.capacidade = capacidade;
    }

    public void arquivar() {
        this.dataArquivado = LocalDate.now();
    }

    public void desarquivar() {
        this.dataArquivado = null;
    }

    public UUID getId() {
        return id;
    }

    public NomeVeiculo getNome() {
        return nome;
    }

    public int getCapacidade() {
        return capacidade;
    }

    public LocalDate getDataArquivado() {
        return dataArquivado;
    }
}
