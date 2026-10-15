package br.com.imbazzu.secretariaTransporte.cadastrais.core.model;

import br.com.imbazzu.secretariaTransporte.cadastrais.core.model.valueobjects.Cpf;
import br.com.imbazzu.secretariaTransporte.compartilhados.valueObjects.Nome;
import br.com.imbazzu.secretariaTransporte.cadastrais.core.model.valueobjects.Telefone;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Motorista {

    private UUID id;

    private Nome nome;

    private Cpf cpf;

    private Telefone telefone;

    private List<UUID> idVeiculos;

    private boolean arquivado;

    private Motorista(UUID id, Nome nome, Cpf cpf,
                      Telefone telefone, List<UUID> idVeiculos, boolean arquivado) {
        Objects.requireNonNull(id,"Id é obrigatório");
        Objects.requireNonNull(nome,"Nome é obrigatório");
        Objects.requireNonNull(cpf,"Cpf é obrigatório");
        Objects.requireNonNull(telefone,"Telefone é obrigatório");

        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.telefone = telefone;
        this.idVeiculos = idVeiculos;
        this.arquivado = arquivado;
    }

    public static Motorista criar(Nome nome, Cpf cpf, Telefone telefone, List<UUID> idVeiculos) {
        return new Motorista(UUID.randomUUID(),nome,cpf,telefone,idVeiculos,false);
    }

    public static Motorista recriar(UUID id, Nome nome,Cpf cpf, Telefone telefone,
                                    List<UUID> idVeiculos, boolean arquivado) {
        return new Motorista(id,nome,cpf,telefone,idVeiculos,arquivado);
    }

    public void editar(Nome nome, Cpf cpf, Telefone telefone, List<UUID> idVeiculos){
        Objects.requireNonNull(id,"Id é obrigatório");
        Objects.requireNonNull(nome,"Nome é obrigatório");
        Objects.requireNonNull(cpf,"Cpf é obrigatório");
        Objects.requireNonNull(telefone,"Telefone é obrigatório");

        alterarNome(nome);
        altearCpf(cpf);
        alterarTelefone(telefone);
        alterarVeiculo(idVeiculos);
    }

    public void alterarNome(Nome nome) {
        Objects.requireNonNull(nome,"Nome é obrigatório");
        this.nome = nome;
    }

    public void altearCpf(Cpf cpf) {
        Objects.requireNonNull(cpf,"CPF é obrigatório");
        this.cpf = cpf;
    }
    public void alterarTelefone(Telefone telefone) {
        Objects.requireNonNull(telefone,"Telefone é obrigatório");
    }

    public void alterarVeiculo(List<UUID> idVeiculos) {
        this.idVeiculos = idVeiculos;
    }

    public void arquivar(){
        this.arquivado = true;
    }

    public void desarquivar(){
        this.arquivado = false;
    }

    public Telefone getTelefone() {
        return telefone;
    }

    public Nome getNome() {
        return nome;
    }

    public Cpf getCpf() {
        return cpf;
    }

    public UUID getId() {
        return id;
    }

    public List<UUID> getIdVeiculos() {
        return List.copyOf(idVeiculos);
    }

    public boolean isArquivado() {
        return arquivado;
    }
}
