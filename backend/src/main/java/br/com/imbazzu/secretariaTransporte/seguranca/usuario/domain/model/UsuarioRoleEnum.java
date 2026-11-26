package br.com.imbazzu.secretariaTransporte.seguranca.usuario.domain.model;

public enum UsuarioRoleEnum {

    USUARIO("USUARIO", 0),
    AGENDAMENTO("AGENDAMENTO", 0),
    MOTORISTA("MOTORISTA", 1),
    GESTAO("GESTAO", 2),
    ADMIN("ADMIN", 3);

    private final String role;
    private final int nivel;

    UsuarioRoleEnum(String role, int nivel) {
        this.role = role;
        this.nivel = nivel;
    }

    public String getRole() {
        return role;
    }

    public int getNivel() {
        return nivel;
    }
}
