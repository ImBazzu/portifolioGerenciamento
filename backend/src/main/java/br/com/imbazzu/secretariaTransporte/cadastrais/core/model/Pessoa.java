package br.com.imbazzu.secretariaTransporte.cadastrais.core.model;

import br.com.imbazzu.secretariaTransporte.cadastrais.core.model.valueobjects.Cpf;
import br.com.imbazzu.secretariaTransporte.compartilhados.valueObjects.Nome;
import br.com.imbazzu.secretariaTransporte.cadastrais.core.model.valueobjects.Telefone;
import br.com.imbazzu.secretariaTransporte.cadastrais.core.model.valueobjects.Endereco;
import br.com.imbazzu.secretariaTransporte.cadastrais.core.model.valueobjects.Observacao;

import java.util.Objects;
import java.util.UUID;


public class Pessoa {

    private UUID id;

    private Nome nome;

    private Cpf cpf;

    private Telefone telefone;

    private Endereco endereco;

    private Observacao observacao;

    private UUID pessoaCondicaoId;

    private boolean arquivado;

    private Pessoa(UUID id, Nome nome, Cpf cpf, Telefone telefone, Endereco endereco, Observacao observacao, UUID pessoaCondicaoId,boolean arquivado) {
        Objects.requireNonNull(id,"Id é obrigatório");
        Objects.requireNonNull(nome,"Nome é obrigatório");
        Objects.requireNonNull(cpf,"Cpf é obrigatório");
        Objects.requireNonNull(telefone,"Telefone é obrigatório");
        Objects.requireNonNull(pessoaCondicaoId, "Id da condição é obrigatório");
        this.id=id;
        this.nome = nome;
        this.cpf = cpf;
        this.telefone = telefone;
        this.endereco = endereco;
        this.observacao = observacao;
        this.pessoaCondicaoId = pessoaCondicaoId;
        this.arquivado=arquivado;
    }

    public static Pessoa criar(Nome nome,Cpf cpf, Telefone telefone, UUID pessoaCondicaoId,Endereco endereco, Observacao observacao) {
        return  new Pessoa(UUID.randomUUID() ,nome, cpf, telefone, endereco, observacao, pessoaCondicaoId,false);
    }

    public static Pessoa recriar(UUID id, Nome nome, Cpf cpf, Telefone telefone, UUID idCondicao,
                                 Endereco endereco, Observacao observacao, boolean arquivado) {
        return new Pessoa(id,nome,cpf,telefone,endereco,observacao,idCondicao,arquivado);
    }

    public void editar(Nome nome, Cpf cpf, Telefone telefone,UUID pessoaCondicaoId, Endereco endereco, Observacao observacao) {
        Objects.requireNonNull(nome,"Nome é obrigatório");
        Objects.requireNonNull(cpf,"Cpf é obrigatório");
        Objects.requireNonNull(telefone,"Telefone é obrigatório");
        Objects.requireNonNull(endereco,"Endereço é obrigatório");
        Objects.requireNonNull(observacao,"Observacao é obrigatório");
        alterarNome(nome);
        alterarCpf(cpf);
        alterarTelefone(telefone);
        alterarPessoaCondicaoId(pessoaCondicaoId);

        alterarEndereco(endereco);
        alterarObservacao(observacao);
    }

    public void alterarNome(Nome nome){
        Objects.requireNonNull(nome,"Nome é obrigatório");
        this.nome = nome;
    }
    public void  alterarCpf(Cpf cpf){
        Objects.requireNonNull(cpf,"Cpf é obrigatório");
        this.cpf = cpf;
    }
    public void alterarTelefone(Telefone telefone){
        Objects.requireNonNull(telefone,"Telefone é obrigatório");
        this.telefone = telefone;
    }
    public void alterarEndereco(Endereco endereco){
        Objects.requireNonNull(endereco,"Endereço é obrigatório");
        this.endereco = endereco;
    }

    public void  alterarObservacao(Observacao observacao){
        Objects.requireNonNull(observacao,"Observação é obrigatório");
        this.observacao = observacao;
    }

    public void alterarPessoaCondicaoId(UUID idCondicao){
        Objects.requireNonNull(idCondicao,"Id condição é obrigatório");
        this.pessoaCondicaoId = idCondicao;
    }

    public void arquivar(){
        this.arquivado=true;
    }

    public void desarquivar(){
        this.arquivado=false;
    }

    //============================
    //Getters
    //============================

    public Nome getNome() {
        return nome;
    }

    public UUID getId() {
        return id;
    }

    public Cpf getCpf() {
        return cpf;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public Observacao getObservacao() {
        return observacao;
    }

    public Telefone getTelefone() {
        return telefone;
    }

    public UUID getPessoaCondicaoId() {
        return pessoaCondicaoId;
    }

    public boolean isArquivado() {
        return arquivado;
    }
}
