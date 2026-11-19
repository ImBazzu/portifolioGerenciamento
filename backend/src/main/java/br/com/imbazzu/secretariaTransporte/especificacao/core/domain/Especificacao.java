package br.com.imbazzu.secretariaTransporte.especificacao.core.domain;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public final class Especificacao {

    private final UUID id;
    private String nome;
    private UUID prioridade;
    private String observacao;
    private LocalDate dataArquivado;

    private Especificacao(UUID id, String nome, UUID prioridade, String observacao, LocalDate dataArquivado) {
        this.id = Objects.requireNonNull(id, "ID é obrigatório");
        this.nome = Objects.requireNonNull(nome, "Nome é obrigatório");
        this.prioridade = Objects.requireNonNull(prioridade, "Prioridade é obrigatória");
        this.observacao = observacao;
        this.dataArquivado = dataArquivado;
    }

    public static Especificacao criar(String nome, UUID prioridade, String observacao) {
        return new Especificacao(UUID.randomUUID(), nome, prioridade, observacao, null);
    }

    public static Especificacao recriar(UUID id, String nome, UUID prioridade, String observacao, LocalDate dataArquivado) {
        return new Especificacao(id, nome, prioridade, observacao, dataArquivado);
    }

    public void editar(String nome, UUID prioridade, String observacao) {
        this.nome = Objects.requireNonNull(nome, "Nome é obrigatório");
        this.prioridade = Objects.requireNonNull(prioridade, "Prioridade é obrigatória");
        this.observacao = observacao;
    }

    public void arquivar() {
        if (dataArquivado == null) {
            dataArquivado = LocalDate.now();
        }
    }

    public void desarquivar() {
        dataArquivado = null;
    }

    public UUID getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public UUID getPrioridade() {
        return prioridade;
    }

    public String getObservacao() {
        return observacao;
    }

    public LocalDate getDataArquivado() {
        return dataArquivado;
    }
}
