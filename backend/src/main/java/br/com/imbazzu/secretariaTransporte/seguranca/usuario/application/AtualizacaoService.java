package br.com.imbazzu.secretariaTransporte.seguranca.usuario.application;

import br.com.imbazzu.secretariaTransporte.seguranca.usuario.domain.excecoes.UsuarioNaoAutorizadoException;
import br.com.imbazzu.secretariaTransporte.seguranca.usuario.domain.porta.in.AtualizarTokenUseCase;
import br.com.imbazzu.secretariaTransporte.seguranca.usuario.domain.porta.out.TokenPorta;
import br.com.imbazzu.secretariaTransporte.seguranca.usuario.domain.porta.out.UsuarioRepositorioPorta;
import br.com.imbazzu.secretariaTransporte.seguranca.usuario.dto.TokenResponseDto;

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
        var dados = tokenPorta.extrairDadosDoTokenAtualizacao(refreshToken);

        var usuario = usuarioRepositorioPorta.buscarPorId(dados.id())
                .orElseThrow(() -> new UsuarioNaoAutorizadoException("Usuário não encontrado."));
        if (usuario.getVersaoToken() != dados.versao()) {
            throw new UsuarioNaoAutorizadoException("Sessão invalidada.");
        }

        return new TokenResponseDto(
                tokenPorta.gerarTokenAcesso(usuario),
                tokenPorta.gerarTokenAtualizacao(usuario)
        );
    }
}
