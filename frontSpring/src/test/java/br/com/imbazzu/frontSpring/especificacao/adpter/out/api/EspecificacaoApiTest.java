package br.com.imbazzu.frontSpring.especificacao.adpter.out.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import java.net.URI;
import java.util.UUID;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import br.com.imbazzu.frontSpring.autenticacao.core.porta.out.RetryComRefreshPorta;
import br.com.imbazzu.frontSpring.autenticacao.infraestrutura.httpConfig.RestClientProtegido;
import br.com.imbazzu.frontSpring.especificacao.core.dto.EspecificacaoRequestDto;
import br.com.imbazzu.frontSpring.pessoa.core.dto.EnumPrioridade;

class EspecificacaoApiTest {
    @Test
    void enviaPrioridadeNoFormatoEsperadoPeloBackend() {
        var builder = RestClient.builder().baseUrl("http://localhost:8080/api");
        var server = MockRestServiceServer.bindTo(builder).build();
        var retry = new RetryComRefreshPorta() {
            @Override
            public <T> T executar(Supplier<T> chamadaApi) {
                return chamadaApi.get();
            }
        };
        var api = new EspecificacaoApi(new RestClientProtegido(builder.build()), retry);
        var id = UUID.fromString("b4d0a150-2d80-4f0b-9a1d-3cfb8d8a0f21");
        server.expect(requestTo("http://localhost:8080/api/especificacao"))
                .andExpect(content().json("""
                        {"nome":"Transporte","prioridade":"ALTA","observacao":"Observação"}
                        """))
                .andRespond(withStatus(HttpStatus.CREATED).location(URI.create("/api/especificacao/" + id)));

        assertEquals(id, api.salvar(new EspecificacaoRequestDto("Transporte", EnumPrioridade.ALTA, "Observação")));
        server.verify();
    }
}
