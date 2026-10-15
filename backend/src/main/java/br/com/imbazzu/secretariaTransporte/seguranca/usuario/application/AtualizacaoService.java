package br.com.imbazzu.secretariaTransporte.seguranca.usuario.application;

import br.com.imbazzu.secretariaTransporte.seguranca.usuario.domain.porta.in.AtualizarTokenUseCase;
import br.com.imbazzu.secretariaTransporte.seguranca.usuario.domain.porta.out.TokenPorta;
import br.com.imbazzu.secretariaTransporte.cadastrais.core.porta.out.UsuarioRepositorioPorta;
import br.com.imbazzu.secretariaTransporte.seguranca.usuario.dto.TokenResponseDto;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class AtualizacaoService implements AtualizarTokenUseCase {

    private final UsuarioRepositorioPorta usuarioRepositorioPorta;
    private final TokenPorta tokenPorta;

    public AtualizacaoService(UsuarioRepositorioPorta usuarioRepositorioPorta,
                              TokenPorta tokenPorta) {
        this.usuarioRepositorioPorta = usuarioRepositorioPorta;
        this.tokenPorta = tokenPorta;
    }

    @Override
    public TokenResponseDto atualizarToken(String refreshToken) {
        String nome = tokenPorta.extrairDadosDoTokenAtualizacao(refreshToken);

        var usuario = usuarioRepositorioPorta.buscarPorNome(nome)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        return new TokenResponseDto(
                tokenPorta.gerarTokenAcesso(usuario),
                tokenPorta.gerarTokenAtualizacao(usuario)
        );
    }
}
