package br.com.imbazzu.secretariaTransporte.seguranca.usuario.domain.model;

import br.com.imbazzu.secretariaTransporte.compartilhados.valueObjects.Nome;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class Usuario {

    private UUID id;

    private Nome nome;

    private String senha;
    private long versaoToken;

    private List<UsuarioRoleEnum> permissoes;

    private Usuario() {}

    public static Usuario criar(Nome nome, String senha, UsuarioRoleEnum permissao ) {
        var  usuario = new Usuario();
        usuario.id = UUID.randomUUID();
        usuario.nome = nome;
        usuario.senha = senha;
        usuario.permissoes = new ArrayList<>();
        usuario.permissoes.add(permissao == UsuarioRoleEnum.USUARIO ? UsuarioRoleEnum.AGENDAMENTO : permissao);
        return usuario;
    }

    public static Usuario reconstruir(UUID id, Nome nome, String senha, List<UsuarioRoleEnum> permissao, long versaoToken) {
        var  usuario = new Usuario();
        usuario.nome = nome;
        usuario.id = id;
        usuario.senha = senha;
        usuario.permissoes = new ArrayList<>(permissao);
        usuario.versaoToken = versaoToken;
        return usuario;
    }

    public List<String> getPermissoes() {
        int nivel = permissoes.stream().mapToInt(UsuarioRoleEnum::getNivel).max().orElse(-1);
        return java.util.Arrays.stream(UsuarioRoleEnum.values())
                .filter(p -> p != UsuarioRoleEnum.USUARIO && p.getNivel() <= nivel)
                .map(permissao -> "ROLE_" + permissao.getRole())
                .toList();
    }

    public List<UsuarioRoleEnum> getPerfisCadastrados() {
        return List.copyOf(permissoes);
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

    public long getVersaoToken() {
        return versaoToken;
    }
}
