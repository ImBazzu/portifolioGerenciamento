package br.com.imbazzu.secretariaTransporte.especificacao.core.excecoes;

public class EspecificacaoDuplicadoException extends RuntimeException {

    public EspecificacaoDuplicadoException(String nome) {
        super("Especificação com nome '%s' já existe".formatted(nome));
    }
}
