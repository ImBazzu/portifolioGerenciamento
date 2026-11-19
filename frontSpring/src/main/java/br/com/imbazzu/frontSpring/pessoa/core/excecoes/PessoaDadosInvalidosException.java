package br.com.imbazzu.frontSpring.pessoa.core.excecoes;

public class PessoaDadosInvalidosException extends RuntimeException {
    public PessoaDadosInvalidosException() {
        super("Os dados informados são inválidos. Confira o CPF, telefone e endereço. A observação deve-se ter no mínimo 5 caracteres.");
    }
}
