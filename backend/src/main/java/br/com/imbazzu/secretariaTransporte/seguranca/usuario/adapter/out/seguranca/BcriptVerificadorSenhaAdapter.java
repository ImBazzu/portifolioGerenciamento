package br.com.imbazzu.secretariaTransporte.seguranca.usuario.adapter.out.seguranca;

import br.com.imbazzu.secretariaTransporte.seguranca.usuario.domain.porta.out.VerificadorSenhaPorta;
import org.springframework.security.crypto.password.PasswordEncoder;

public class BcriptVerificadorSenhaAdapter implements VerificadorSenhaPorta {

    private final PasswordEncoder encoder;

    public BcriptVerificadorSenhaAdapter(PasswordEncoder encoder) {
        this.encoder = encoder;
    }

    @Override
    public boolean verificar(String senhaRaw, String senhaHash) {
        return encoder.matches(senhaRaw, senhaHash);
    }
}
