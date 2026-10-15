package br.com.imbazzu.secretariaTransporte.seguranca.usuario.adapter.out.seguranca;

import br.com.imbazzu.secretariaTransporte.seguranca.usuario.domain.porta.out.CriptografiaSenhaPorta;
import org.springframework.security.crypto.password.PasswordEncoder;

public class CriptografarSenhaAdapter implements CriptografiaSenhaPorta {

    private final PasswordEncoder encoder;

    public CriptografarSenhaAdapter(PasswordEncoder encoder) {
        this.encoder = encoder;
    }

    @Override
    public String criptografar(String senha) {
        return encoder.encode(senha);
    }
}
