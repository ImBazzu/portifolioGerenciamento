package br.com.imbazzu.secretariaTransporte.cadastrais.core.porta.in;


public interface UsuarioApplicationPorta {

    void cadastrar(String nome, String senha, String usuarioRole);
}

