package br.com.imbazzu.secretariaTransporte.seguranca.usuario.domain.model;

public enum UsuarioRoleEnum {

    ADMIN("ADMIN"),
    USUARIO("USUARIO");

    private String role;

    UsuarioRoleEnum(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}
