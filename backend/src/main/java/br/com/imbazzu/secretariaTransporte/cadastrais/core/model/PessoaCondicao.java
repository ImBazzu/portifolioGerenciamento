package br.com.imbazzu.secretariaTransporte.cadastrais.core.model;

import br.com.imbazzu.secretariaTransporte.compartilhados.valueObjects.Nome;

import java.util.Objects;
import java.util.UUID;




public class PessoaCondicao {

    private UUID id;

    private Nome nome;

    private boolean arquivado;

    private PessoaCondicao(UUID id, Nome nome, boolean arquivado) {

        Objects.requireNonNull(id,"ID é obrigatório");
        Objects.requireNonNull(nome,"Nome é obrigatório");
        this.id = id;
        this.nome = nome;
        this.arquivado = arquivado;
    }

    public static PessoaCondicao criar(Nome nome) {
        return new PessoaCondicao(UUID.randomUUID(),nome,true);
    }

    public static PessoaCondicao recriar(UUID id, Nome nome, boolean arquivado) {
        return new PessoaCondicao(id, nome,arquivado);
    }

    public void editar(Nome nome) {
        alterarNome(nome);
    }

    public void arquivar(){
        this.arquivado = true;
    }

    public void desarquivar(){
        this.arquivado = false;
    }

    public void alterarNome(Nome nome) {
        this.nome = nome;
    }

    public UUID getId() {
        return id;
    }

    public Nome getNome() {
        return nome;
    }

    public boolean isArquivado() {
        return arquivado;
    }
}

