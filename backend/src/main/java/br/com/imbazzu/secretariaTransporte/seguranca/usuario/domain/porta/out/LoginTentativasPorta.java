package br.com.imbazzu.secretariaTransporte.seguranca.usuario.domain.porta.out;

public interface LoginTentativasPorta {

    void validarBloqueio(String chave);

    void registrarFalha(String chave);

    void limparTentativas(String chave);
}
