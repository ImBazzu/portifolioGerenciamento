package br.com.imbazzu.frontSpring.relatorio.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.time.LocalDate;
import java.time.LocalTime;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.imbazzu.frontSpring.autenticacao.core.porta.out.RetryComRefreshPorta;
import br.com.imbazzu.frontSpring.autenticacao.infraestrutura.httpConfig.RestClientProtegido;

class InvestigacaoApiTest {
    @Test
    void encaminhaFiltrosEDesserializaDetalhesDoMotorista() {
        var builder = RestClient.builder().baseUrl("http://localhost:8080/api");
        var server = MockRestServiceServer.bindTo(builder).build();
        var api = new InvestigacaoApi(new RestClientProtegido(builder.build()), new RetryComRefreshPorta() {
            public <T> T executar(Supplier<T> chamada) { return chamada.get(); }
        });
        var motorista = UUID.randomUUID();
        var veiculo = UUID.randomUUID();
        server.expect(request -> {
            assertEquals("/api/relatorios/investigacao/motoristas", request.getURI().getPath());
            var params = UriComponentsBuilder.fromUri(request.getURI()).build().getQueryParams();
            assertEquals(motorista.toString(), params.getFirst("motorista"));
            assertEquals("2030-01-01", params.getFirst("inicio"));
            assertEquals("Cidade + Centro", URLDecoder.decode(params.getFirst("cidade"), StandardCharsets.UTF_8));
            assertEquals("Ana & Bia", URLDecoder.decode(params.getFirst("nome"), StandardCharsets.UTF_8));
            assertEquals("1", params.getFirst("pagina"));
            assertEquals("2", params.getFirst("resumoPagina"));
        }).andRespond(withSuccess("""
                {"resumo":{"motoristas":1,"veiculos":1,"passageiros":1},
                 "porMotorista":{"content":[],"totalElements":0,"totalPages":0,"number":2},
                 "veiculos":{"content":[{"id":"%s","data":"2030-01-01","motorista":"Ana","categoria":"Dia",
                 "tipo":"Van","identificacao":null,"saida":"07:00:00","saidaCalculada":true,
                 "passageiros":[{"nome":"Bia","cidade":"Cidade","destino":"Hospital",
                 "chegada":"09:00:00","acompanhantes":1}]}],"totalElements":1,"totalPages":1,"number":1}}
                """.formatted(veiculo), MediaType.APPLICATION_JSON));
        var resultado = api.motoristas("Ana & Bia", motorista, "Dia",
                new InvestigacaoApi.Filtros(LocalDate.of(2030, 1, 1), null, "Cidade + Centro", ""), 1, 2);
        assertEquals(LocalTime.of(7, 0), resultado.veiculos().content().getFirst().saida());
        assertEquals("Hospital", resultado.veiculos().content().getFirst().passageiros().getFirst().destino());
        server.verify();
    }
}
