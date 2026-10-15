package br.com.imbazzu.secretariaTransporte.seguranca.usuario.domain.porta.in;

import br.com.imbazzu.secretariaTransporte.seguranca.usuario.dto.TokenResponseDto;

public interface AutenticarUsuarioUseCase {

    TokenResponseDto autenticar(String login, String senha, String ipOrigem);

}
