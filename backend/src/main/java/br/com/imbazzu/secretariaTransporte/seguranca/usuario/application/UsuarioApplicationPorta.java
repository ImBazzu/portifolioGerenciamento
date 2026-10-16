package br.com.imbazzu.secretariaTransporte.seguranca.usuario.application;


public interface UsuarioApplicationPorta {

    void cadastrar(String nome, String senha, String usuarioRole);
}

