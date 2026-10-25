package br.com.imbazzu.frontSpring.pessoa.adpter.out.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import java.util.UUID;
import java.util.function.Supplier;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import br.com.imbazzu.frontSpring.autenticacao.core.porta.out.RetryComRefreshPorta;
import br.com.imbazzu.frontSpring.autenticacao.infraestrutura.httpConfig.RestClientProtegido;
import br.com.imbazzu.frontSpring.pessoa.core.dto.PessoaPrioridade;
import br.com.imbazzu.frontSpring.pessoa.core.dto.PessoaRequestDto;

class PessoaApiTest {
    private MockRestServiceServer server;
    private PessoaApi api;

    @BeforeEach
    void configurar() {
        var builder = RestClient.builder().baseUrl("http://localhost:8080/api");
        server = MockRestServiceServer.bindTo(builder).build();
        api = new PessoaApi(new RestClientProtegido(builder.build()), executarSemRefresh());
    }

    private RetryComRefreshPorta executarSemRefresh() {
        return new RetryComRefreshPorta() {
            @Override
            public <T> T executar(Supplier<T> chamadaApi) {
                return chamadaApi.get();
            }
        };
    }

    @AfterEach
    void verificarRequisicoes() {
        server.verify();
    }

    @Test
    void listaPessoasAtivasEConvertePrioridade() {
        server.expect(requestTo("http://localhost:8080/api/pessoa/listar?nome=Ana&pagina=0&tamanho=10"))
                .andRespond(withSuccess("""
                        {
                          "conteudo": [{
                            "id": "b4d0a150-2d80-4f0b-9a1d-3cfb8d8a0f21",
                            "nome": "Ana Souza",
                            "observacao": null,
                            "prioridade": "ALTA",
                            "dataArquivado": null,
                            "endereco": "Rua das Flores, 123",
                            "cpf": "52998224725",
                            "telefone": "11987654321"
                          }],
                          "paginaAtual": 0,
                          "tamanhoPagina": 10,
                          "totalElementos": 1,
                          "totalPaginas": 1
                        }
                        """, MediaType.APPLICATION_JSON));

        var page = api.listar("Ana", false, 0, 10);

        assertEquals(1, page.totalElementos());
        assertEquals(PessoaPrioridade.ALTA, page.conteudo().getFirst().prioridade());
        assertNull(page.conteudo().getFirst().dataArquivado());
    }

    @Test
    void listaPessoasArquivadasPeloEndpointDoBackend() {
        server.expect(requestTo("http://localhost:8080/api/pessoa/listar/arquivado?nome=&pagina=1&tamanho=10"))
                .andRespond(withSuccess("""
                        {
                          "conteudo": [],
                          "paginaAtual": 1,
                          "tamanhoPagina": 10,
                          "totalElementos": 0,
                          "totalPaginas": 0
                        }
                        """, MediaType.APPLICATION_JSON));

        assertEquals(0, api.listar("", true, 1, 10).totalElementos());
    }

    @Test
    void salvarEnviaOsCamposEsperadosPeloBackend() {
        server.expect(requestTo("http://localhost:8080/api/pessoa"))
                .andExpect(content().json("""
                        {
                          "nome":"Ana Souza",
                          "observacao":"",
                          "prioridade":"MODERADA",
                          "endereco":"Rua das Flores, 123",
                          "cpf":"52998224725",
                          "telefone":"11987654321"
                        }
                        """))
                .andRespond(withStatus(HttpStatus.CREATED));

        api.salvar(new PessoaRequestDto(
                "Ana Souza", "", PessoaPrioridade.MODERADA, "Rua das Flores, 123", "52998224725", "11987654321"));
    }

    @Test
    void arquivaPessoaComPatchNoEndpointCorrespondente() {
        var id = UUID.fromString("b4d0a150-2d80-4f0b-9a1d-3cfb8d8a0f21");
        server.expect(requestTo("http://localhost:8080/api/pessoa/" + id + "/arquivar"))
                .andRespond(withStatus(HttpStatus.NO_CONTENT));

        api.arquivar(id);
    }
}
