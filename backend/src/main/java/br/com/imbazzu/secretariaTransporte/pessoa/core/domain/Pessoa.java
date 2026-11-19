package br.com.imbazzu.secretariaTransporte.pessoa.core.domain;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

import br.com.imbazzu.secretariaTransporte.cadastrais.core.model.valueobjects.Cpf;
import br.com.imbazzu.secretariaTransporte.cadastrais.core.model.valueobjects.Endereco;
import br.com.imbazzu.secretariaTransporte.cadastrais.core.model.valueobjects.Observacao;
import br.com.imbazzu.secretariaTransporte.cadastrais.core.model.valueobjects.Telefone;
import br.com.imbazzu.secretariaTransporte.compartilhados.valueObjects.Nome;
import br.com.imbazzu.secretariaTransporte.especificacao.core.domain.Especificacao;

public final class Pessoa {

    private final UUID id;
    private Nome nome;
    private Observacao observacao;
    private Especificacao especificacao;
    private boolean buscarNaResidencia;
    private int quantidadeAcompanhantes;
    private LocalDate dataArquivado;
    private Endereco endereco;
    private Cpf cpf;
    private Telefone telefone;

    private Pessoa(UUID id, Nome nome, Observacao observacao, Especificacao especificacao,
            boolean buscarNaResidencia, int quantidadeAcompanhantes, LocalDate dataArquivado, 
            Endereco endereco, Cpf cpf, Telefone telefone) {
        this.id = Objects.requireNonNull(id, "ID é obrigatório");
        this.nome = Objects.requireNonNull(nome, "Nome é obrigatório");
        this.observacao = observacao;
        this.especificacao = Objects.requireNonNull(especificacao, "Especificação é obrigatória");
        this.buscarNaResidencia = buscarNaResidencia;
        this.quantidadeAcompanhantes = quantidadeAcompanhantes;
        this.dataArquivado = dataArquivado;
        this.endereco = endereco;
        this.cpf = cpf;
        this.telefone = Objects.requireNonNull(telefone, "Telefone é obrigatório");
    }

    public static Pessoa criar(Nome nome, Observacao observacao, Especificacao especificacao,
            boolean buscarNaResidencia, int quantidadeAcompanhantes, Endereco endereco, Cpf cpf, Telefone telefone) {
        return new Pessoa(UUID.randomUUID(), nome, observacao, especificacao, buscarNaResidencia, 
            quantidadeAcompanhantes, null, endereco, cpf, telefone);
    }

    public static Pessoa recriar(UUID id, Nome nome, Observacao observacao, Especificacao especificacao,
            boolean buscarNaResidencia, int quantidadeAcompanhantes, LocalDate dataArquivado, 
            Endereco endereco, Cpf cpf, Telefone telefone) {
        return new Pessoa(id, nome, observacao, especificacao, buscarNaResidencia, quantidadeAcompanhantes,
            dataArquivado, endereco, cpf, telefone);
    }

    public void editar(Nome nome, Observacao observacao, Especificacao especificacao,
            boolean buscarNaResidencia, int quantidadeAcompanhantes, Endereco endereco, Cpf cpf, Telefone telefone) {
        this.nome = Objects.requireNonNull(nome, "Nome é obrigatório");
        this.observacao = observacao;
        this.especificacao = Objects.requireNonNull(especificacao, "Especificação é obrigatória");
        this.buscarNaResidencia = buscarNaResidencia;
        this.quantidadeAcompanhantes = quantidadeAcompanhantes;
        this.endereco = endereco;
        this.cpf = cpf;
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

    public Especificacao getEspecificacao() {
        return especificacao;
    }

    public boolean isBuscarNaResidencia() {
        return buscarNaResidencia;
    }

    public int getQuantidadeAcompanhantes() {
        return quantidadeAcompanhantes;
    }

    public LocalDate getDataArquivado() {
        return dataArquivado;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public Cpf getCpf() {
        return cpf != null ? cpf : null;
    }

    public Telefone getTelefone() {
        return telefone;
    }
}
