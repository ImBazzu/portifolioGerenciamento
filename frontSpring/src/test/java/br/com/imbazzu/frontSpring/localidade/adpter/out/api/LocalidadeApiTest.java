package br.com.imbazzu.frontSpring.localidade.adpter.out.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import java.net.URI;
import java.util.UUID;
import java.util.function.Supplier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import br.com.imbazzu.frontSpring.autenticacao.core.porta.out.RetryComRefreshPorta;
import br.com.imbazzu.frontSpring.autenticacao.infraestrutura.httpConfig.RestClientProtegido;
import br.com.imbazzu.frontSpring.localidade.core.dto.DestinoRequestDto;

class LocalidadeApiTest {

    private MockRestServiceServer server;
    private LocalidadeApi api;

    @BeforeEach
    void configurar() {
        var builder = RestClient.builder().baseUrl("http://localhost:8080/api");
        server = MockRestServiceServer.bindTo(builder).build();
        api = new LocalidadeApi(new RestClientProtegido(builder.build()), executarSemRefresh());
    }

    private RetryComRefreshPorta executarSemRefresh() {
        return new RetryComRefreshPorta() {
            @Override
            public <T> T executar(Supplier<T> chamadaApi) {
                return chamadaApi.get();
            }
        };
    }

    @Test
    void salvarDestinoRetornaIdCriadoPeloBackend() {
        var idCidade = UUID.fromString("6b0d3800-2ca7-4e1f-bfb3-36f849104d83");
        var idDestino = UUID.fromString("ea5bb91c-4faa-4dfa-85f7-96fcbf849497");
        server.expect(requestTo("http://localhost:8080/api/destino"))
                .andExpect(content().json("""
                        {"idCidade":"6b0d3800-2ca7-4e1f-bfb3-36f849104d83","nome":"Hospital Municipal"}
                        """))
                .andRespond(withStatus(HttpStatus.CREATED).location(URI.create("/destino/" + idDestino)));

        assertEquals(idDestino, api.salvarDestino(new DestinoRequestDto(idCidade, "Hospital Municipal")));
        server.verify();
    }
}
