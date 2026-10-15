package br.com.imbazzu.secretariaTransporte.seguranca.usuario.domain.porta.out;

public interface VerificadorSenhaPorta {
    boolean verificar(String senhaRaw, String senhaHash);
}
