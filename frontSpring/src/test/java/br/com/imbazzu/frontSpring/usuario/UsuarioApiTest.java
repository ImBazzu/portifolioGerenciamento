package br.com.imbazzu.frontSpring.usuario;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import br.com.imbazzu.frontSpring.autenticacao.core.porta.out.RetryComRefreshPorta;
import br.com.imbazzu.frontSpring.autenticacao.core.porta.out.SessionAuthPorta;
import br.com.imbazzu.frontSpring.autenticacao.infraestrutura.httpConfig.RestClientProtegido;

class UsuarioApiTest {
    @Test
    void atualizacaoDaContaSalvaNovosTokensNaSessao() {
        var builder = RestClient.builder().baseUrl("http://localhost:8080/api");
        var server = MockRestServiceServer.bindTo(builder).build();
        var sessao = mock(SessionAuthPorta.class);
        var api = new UsuarioApi(new RestClientProtegido(builder.build()), new RetryComRefreshPorta() {
            public <T> T executar(Supplier<T> chamada) { return chamada.get(); }
        }, sessao);
        server.expect(requestTo("http://localhost:8080/api/usuarios/eu"))
                .andExpect(method(HttpMethod.PUT))
                .andExpect(content().json("{\"nome\":\"Ana\",\"senhaAtual\":\"antiga\",\"novaSenha\":\"novaSenha123\"}"))
                .andRespond(withSuccess("{\"tokenAcesso\":\"novo acesso\",\"tokenAtualizar\":\"novo refresh\"}",
                        MediaType.APPLICATION_JSON));
        api.atualizarProprio("Ana", "antiga", "novaSenha123");
        verify(sessao).store("novo acesso", "novo refresh");
        server.verify();
    }
}
