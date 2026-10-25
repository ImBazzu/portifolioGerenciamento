package br.com.imbazzu.secretariaTransporte.pessoa.core.domain;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

import br.com.imbazzu.secretariaTransporte.cadastrais.core.model.valueobjects.Cpf;
import br.com.imbazzu.secretariaTransporte.cadastrais.core.model.valueobjects.Endereco;
import br.com.imbazzu.secretariaTransporte.cadastrais.core.model.valueobjects.Observacao;
import br.com.imbazzu.secretariaTransporte.cadastrais.core.model.valueobjects.Telefone;
import br.com.imbazzu.secretariaTransporte.compartilhados.valueObjects.Nome;

public final class Pessoa {

    private final UUID id;
    private Nome nome;
    private Observacao observacao;
    private EnumPrioridade prioridade;
    private LocalDate dataArquivado;
    private Endereco endereco;
    private Cpf cpf;
    private Telefone telefone;

    private Pessoa(UUID id, Nome nome, Observacao observacao, EnumPrioridade prioridade,
            LocalDate dataArquivado, Endereco endereco, Cpf cpf, Telefone telefone) {
        this.id = Objects.requireNonNull(id, "ID é obrigatório");
        this.nome = Objects.requireNonNull(nome, "Nome é obrigatório");
        this.observacao = observacao;
        this.prioridade = Objects.requireNonNull(prioridade, "Prioridade é obrigatória");
        this.dataArquivado = dataArquivado;
        this.endereco = Objects.requireNonNull(endereco, "Endereço é obrigatório");
        this.cpf = Objects.requireNonNull(cpf, "CPF é obrigatório");
        this.telefone = Objects.requireNonNull(telefone, "Telefone é obrigatório");
    }

    public static Pessoa criar(Nome nome, Observacao observacao, EnumPrioridade prioridade,
            Endereco endereco, Cpf cpf, Telefone telefone) {
        return new Pessoa(UUID.randomUUID(), nome, observacao, prioridade, null, endereco, cpf, telefone);
    }

    public static Pessoa recriar(UUID id, Nome nome, Observacao observacao, EnumPrioridade prioridade,
            LocalDate dataArquivado, Endereco endereco, Cpf cpf, Telefone telefone) {
        return new Pessoa(id, nome, observacao, prioridade, dataArquivado, endereco, cpf, telefone);
    }

    public void editar(Nome nome, Observacao observacao, EnumPrioridade prioridade,
            Endereco endereco, Cpf cpf, Telefone telefone) {
        this.nome = Objects.requireNonNull(nome, "Nome é obrigatório");
        this.observacao = observacao;
        this.prioridade = Objects.requireNonNull(prioridade, "Prioridade é obrigatória");
        this.endereco = Objects.requireNonNull(endereco, "Endereço é obrigatório");
        this.cpf = Objects.requireNonNull(cpf, "CPF é obrigatório");
        this.telefone = Objects.requireNonNull(telefone, "Telefone é obrigatório");
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

    public Nome getNome() {
        return nome;
    }

    public Observacao getObservacao() {
        return observacao;
    }

    public EnumPrioridade getPrioridade() {
        return prioridade;
    }

    public LocalDate getDataArquivado() {
        return dataArquivado;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public Cpf getCpf() {
        return cpf;
    }

    public Telefone getTelefone() {
        return telefone;
    }
}
