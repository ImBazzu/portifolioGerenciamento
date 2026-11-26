package br.com.imbazzu.frontSpring.motorista;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import java.util.UUID;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import br.com.imbazzu.frontSpring.autenticacao.core.porta.out.RetryComRefreshPorta;
import br.com.imbazzu.frontSpring.autenticacao.infraestrutura.httpConfig.RestClientProtegido;

class MotoristaApiTest {
    @Test
    void filtraEDesserializaPaginaEAtribuiMotorista() {
        var builder = RestClient.builder().baseUrl("http://localhost:8080/api");
        var server = MockRestServiceServer.bindTo(builder).build();
        var api = new MotoristaApi(new RestClientProtegido(builder.build()), new RetryComRefreshPorta() {
            public <T> T executar(Supplier<T> chamada) { return chamada.get(); }
        });
        var categoria = UUID.randomUUID();
        var motorista = UUID.randomUUID();
        var veiculo = UUID.randomUUID();
        server.expect(requestTo("http://localhost:8080/api/motoristas?nome=Ana&pagina=1&categoria=" + categoria))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        {"content":[{"id":"%s","nome":"Ana","telefone":"11987654321",
                        "categoriaId":"%s","categoriaNome":"Noturno"}],"totalPages":2,"number":1}
                        """.formatted(motorista, categoria), MediaType.APPLICATION_JSON));
        server.expect(requestTo("http://localhost:8080/api/motoristas/veiculos/" + veiculo))
                .andExpect(method(HttpMethod.PUT))
                .andExpect(content().json("{\"motoristaId\":\"" + motorista + "\"}"))
                .andRespond(withSuccess());
        var resultado = api.buscar("Ana", categoria, 1);
        assertEquals("Ana", resultado.content().getFirst().nome());
        assertEquals(2, resultado.totalPages());
        api.atribuir(veiculo, motorista);
        server.verify();
    }
}
