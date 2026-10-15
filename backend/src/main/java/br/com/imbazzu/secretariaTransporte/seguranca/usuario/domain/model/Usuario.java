package br.com.imbazzu.secretariaTransporte.seguranca.usuario.domain.model;

import br.com.imbazzu.secretariaTransporte.compartilhados.valueObjects.Nome;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


public class Usuario {

    private UUID id;

    private Nome nome;

    private String senha;

    private List<UsuarioRoleEnum> permissoes;

    private Usuario() {}

    public static Usuario criar(Nome nome, String senha, UsuarioRoleEnum permissao ) {
        var  usuario = new Usuario();
        usuario.id = UUID.randomUUID();
        usuario.nome = nome;
        usuario.senha = senha;
        usuario.permissoes = new ArrayList<>();
        usuario.permissoes.add(UsuarioRoleEnum.USUARIO);
        if(permissao == UsuarioRoleEnum.ADMIN) {
            usuario.permissoes.add(UsuarioRoleEnum.ADMIN);
        }
        return usuario;
    }

    public static Usuario reconstruir(UUID id,Nome nome, String senha, List<UsuarioRoleEnum> permissao) {
        var  usuario = new Usuario();
        usuario.nome = nome;
        usuario.id = id;
        usuario.senha = senha;
        usuario.permissoes = new ArrayList<>(permissao);
        return usuario;
    }

    public List<String> getPermissoes() {
        return this.permissoes.stream()
                .map(permissao -> "ROLE_" + permissao.getRole())
                .collect(Collectors.toCollection(ArrayList::new));
    }


    public Nome getNome() {
        return nome;
    }

    public UUID getId() {
        return id;
    }

    public String getSenha() {
        return senha;
    }
}
