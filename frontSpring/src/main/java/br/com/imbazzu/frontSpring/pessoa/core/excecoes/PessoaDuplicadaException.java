package br.com.imbazzu.frontSpring.pessoa.core.excecoes;

public class PessoaDuplicadaException extends RuntimeException {
    public PessoaDuplicadaException() {
        super("Já existe uma pessoa cadastrada com este CPF.");
    }
}
