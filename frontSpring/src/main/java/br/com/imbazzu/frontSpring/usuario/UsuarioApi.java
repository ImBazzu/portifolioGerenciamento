package br.com.imbazzu.frontSpring.usuario;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import br.com.imbazzu.frontSpring.autenticacao.core.dto.LoginResponseDto;
import br.com.imbazzu.frontSpring.autenticacao.core.porta.out.RetryComRefreshPorta;
import br.com.imbazzu.frontSpring.autenticacao.core.porta.out.SessionAuthPorta;
import br.com.imbazzu.frontSpring.autenticacao.infraestrutura.httpConfig.RestClientProtegido;

@Service
public class UsuarioApi {
    public record Usuario(UUID id, String nome, String perfil) {}
    public record Pagina(List<Usuario> content, int totalPages, int number) {}
    public record CriarRequest(String nome, String senha, String perfil) {}
    public record PerfilRequest(String perfil) {}
    public record ProprioRequest(String nome, String senhaAtual, String novaSenha) {}
    private final RestClient api;
    private final RetryComRefreshPorta retry;
    private final SessionAuthPorta sessao;

    public UsuarioApi(RestClientProtegido api, RetryComRefreshPorta retry, SessionAuthPorta sessao) {
        this.api = api.get();
        this.retry = retry;
        this.sessao = sessao;
    }

    public Usuario proprio() {
        return executar(() -> api.get().uri("/usuarios/eu").retrieve().body(Usuario.class));
    }

    public Pagina listar(String nome, int pagina) {
        return executar(() -> api.get().uri(uri -> uri.path("/usuarios").queryParam("nome", nome)
                .queryParam("pagina", pagina).build()).retrieve().body(Pagina.class));
    }

    public void criar(String nome, String senha, String perfil) {
        executar(() -> api.post().uri("/usuarios").body(new CriarRequest(nome, senha, perfil))
                .retrieve().toBodilessEntity());
    }

    public void alterarPerfil(UUID id, String perfil) {
        executar(() -> api.put().uri("/usuarios/{id}/perfil", id).body(new PerfilRequest(perfil))
                .retrieve().toBodilessEntity());
    }

    public void atualizarProprio(String nome, String senhaAtual, String novaSenha) {
        var resposta = executar(() -> api.put().uri("/usuarios/eu")
                .body(new ProprioRequest(nome, senhaAtual, novaSenha)).retrieve().body(LoginResponseDto.class));
        if (resposta == null || resposta.tokenAcesso() == null || resposta.tokenAtualizar() == null) {
            throw new IllegalStateException("Conta alterada, mas a API não retornou os tokens da sessão.");
        }
        sessao.store(resposta.tokenAcesso(), resposta.tokenAtualizar());
    }

    private <T> T executar(Supplier<T> chamada) {
        return retry.executar(chamada);
    }
}
