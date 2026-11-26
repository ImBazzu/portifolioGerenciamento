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
    private boolean validada;
    private LocalDate dataArquivado;
    private Endereco endereco;
    private Cpf cpf;
    private Telefone telefone;
    private final UUID autorId;
    private final String autorNome;

    private Pessoa(UUID id, Nome nome, Observacao observacao, Especificacao especificacao,
            boolean buscarNaResidencia, int quantidadeAcompanhantes, boolean validada, LocalDate dataArquivado,
            Endereco endereco, Cpf cpf, Telefone telefone, UUID autorId, String autorNome) {
        this.id = Objects.requireNonNull(id, "ID é obrigatório");
        this.nome = Objects.requireNonNull(nome, "Nome é obrigatório");
        this.observacao = observacao;
        this.especificacao = Objects.requireNonNull(especificacao, "Especificação é obrigatória");
        this.buscarNaResidencia = buscarNaResidencia;
        this.quantidadeAcompanhantes = quantidadeAcompanhantes;
        this.validada = validada;
        this.dataArquivado = dataArquivado;
        this.endereco = endereco;
        this.cpf = cpf;
        this.telefone = Objects.requireNonNull(telefone, "Telefone é obrigatório");
        this.autorId = autorId;
        this.autorNome = autorNome;
    }

    public static Pessoa criar(Nome nome, Observacao observacao, Especificacao especificacao,
            boolean buscarNaResidencia, int quantidadeAcompanhantes, Endereco endereco, Cpf cpf, Telefone telefone) {
        return criar(nome, observacao, especificacao, buscarNaResidencia, quantidadeAcompanhantes,
                endereco, cpf, telefone, null);
    }

    public static Pessoa criar(Nome nome, Observacao observacao, Especificacao especificacao,
            boolean buscarNaResidencia, int quantidadeAcompanhantes, Endereco endereco, Cpf cpf, Telefone telefone,
            UUID autorId) {
        return new Pessoa(UUID.randomUUID(), nome, observacao, especificacao, buscarNaResidencia,
            quantidadeAcompanhantes, false, null, endereco, cpf, telefone, autorId, null);
    }

    public static Pessoa recriar(UUID id, Nome nome, Observacao observacao, Especificacao especificacao,
            boolean buscarNaResidencia, int quantidadeAcompanhantes, boolean validada, LocalDate dataArquivado,
            Endereco endereco, Cpf cpf, Telefone telefone, UUID autorId, String autorNome) {
        return new Pessoa(id, nome, observacao, especificacao, buscarNaResidencia, quantidadeAcompanhantes,
            validada, dataArquivado, endereco, cpf, telefone, autorId, autorNome);
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
        this.validada = false;
    }

    public void validar() {
        this.validada = true;
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

    public boolean isValidada() {
        return validada;
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

    public UUID getAutorId() { return autorId; }
    public String getAutorNome() { return autorNome; }
}
