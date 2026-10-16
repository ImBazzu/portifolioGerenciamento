package br.com.imbazzu.frontSpring.autenticacao.core.porta.out;

import br.com.imbazzu.frontSpring.autenticacao.core.dto.LoginRefreshDto;
import br.com.imbazzu.frontSpring.autenticacao.core.dto.LoginRequestDto;
import br.com.imbazzu.frontSpring.autenticacao.core.dto.LoginResponseDto;

public interface LoginApiPorta {

    LoginResponseDto login(LoginRequestDto dto);

    LoginResponseDto refresh(LoginRefreshDto refreshToken);

}
