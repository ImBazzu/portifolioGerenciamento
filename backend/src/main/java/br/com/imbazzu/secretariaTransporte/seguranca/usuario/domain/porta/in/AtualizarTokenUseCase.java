package br.com.imbazzu.secretariaTransporte.seguranca.usuario.domain.porta.in;

import br.com.imbazzu.secretariaTransporte.seguranca.usuario.dto.TokenResponseDto;

public interface AtualizarTokenUseCase {

    TokenResponseDto atualizarToken(String tokenAcesso);

}
