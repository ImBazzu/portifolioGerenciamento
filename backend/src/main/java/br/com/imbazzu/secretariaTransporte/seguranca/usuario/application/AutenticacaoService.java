package br.com.imbazzu.secretariaTransporte.seguranca.usuario.application;

import br.com.imbazzu.secretariaTransporte.seguranca.usuario.domain.excecoes.UsuarioNaoAutorizadoException;
import br.com.imbazzu.secretariaTransporte.seguranca.usuario.domain.porta.in.AutenticarUsuarioUseCase;
import br.com.imbazzu.secretariaTransporte.seguranca.usuario.domain.porta.out.LoginTentativasPorta;
import br.com.imbazzu.secretariaTransporte.seguranca.usuario.domain.porta.out.TokenPorta;
import br.com.imbazzu.secretariaTransporte.cadastrais.core.porta.out.UsuarioRepositorioPorta;
import br.com.imbazzu.secretariaTransporte.seguranca.usuario.domain.porta.out.VerificadorSenhaPorta;
import br.com.imbazzu.secretariaTransporte.seguranca.usuario.dto.TokenResponseDto;

public class AutenticacaoService implements AutenticarUsuarioUseCase {

    private final UsuarioRepositorioPorta usuarioRepositorioPorta;
    private final TokenPorta tokenPorta;
    private final LoginTentativasPorta loginTentativasPorta;
    private final VerificadorSenhaPorta verificadorSenhaPorta;

    public AutenticacaoService(UsuarioRepositorioPorta usuarioRepositorioPorta,
            TokenPorta tokenPorta, LoginTentativasPorta loginTentativasPorta,
            VerificadorSenhaPorta verificadorSenhaPorta) {
        this.usuarioRepositorioPorta = usuarioRepositorioPorta;
        this.tokenPorta = tokenPorta;
        this.loginTentativasPorta = loginTentativasPorta;
        this.verificadorSenhaPorta = verificadorSenhaPorta;
    }

    @Override
    public TokenResponseDto autenticar(String login, String senha, String ipOrigem) {
        String chave = ipOrigem + ":" + login;
        loginTentativasPorta.validarBloqueio(chave);

        var usuario = usuarioRepositorioPorta.buscarPorNome(login)
                .orElseThrow(() -> {
                    loginTentativasPorta.registrarFalha(chave);
                    return new UsuarioNaoAutorizadoException("Usuário não encontrado");
                });

        if (!verificadorSenhaPorta.verificar(senha, usuario.getSenha())) {
            loginTentativasPorta.registrarFalha(chave);
            throw new UsuarioNaoAutorizadoException("Erro ao autenticar");
        }

        loginTentativasPorta.limparTentativas(chave);

        return new TokenResponseDto(
                tokenPorta.gerarTokenAcesso(usuario),
                tokenPorta.gerarTokenAtualizacao(usuario));
    }

}
