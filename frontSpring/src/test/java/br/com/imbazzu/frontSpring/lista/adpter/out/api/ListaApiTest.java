package br.com.imbazzu.frontSpring.lista.adpter.out.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withCreatedEntity;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import br.com.imbazzu.frontSpring.autenticacao.infraestrutura.httpConfig.RestClientProtegido;
import br.com.imbazzu.frontSpring.autenticacao.core.porta.out.RetryComRefreshPorta;
import br.com.imbazzu.frontSpring.autenticacao.adapter.out.retry.RetryComRefresh;
import br.com.imbazzu.frontSpring.autenticacao.core.porta.in.LoginApplicationPorta;
import br.com.imbazzu.frontSpring.autenticacao.core.exception.TokenInvalidadoException;
import br.com.imbazzu.frontSpring.lista.core.dto.ListaRequestDto;
import br.com.imbazzu.frontSpring.lista.core.dto.ListaRegrasRequestDto;

class ListaApiTest {

    private MockRestServiceServer server;
    private ListaApi api;
    private RestClient.Builder clientBuilder;

    @BeforeEach
    void configurar() {
        clientBuilder = RestClient.builder()
                .baseUrl("http://localhost:8080/api")
                .defaultStatusHandler(
                        status -> status.value() == HttpStatus.UNAUTHORIZED.value(),
                        (request, response) -> {
                            throw new TokenInvalidadoException();
                        });
        server = MockRestServiceServer.bindTo(clientBuilder).build();
        api = new ListaApi(new RestClientProtegido(clientBuilder.build()), executarSemRefresh());
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
    void listaAtivasConsultaApiEConverteResumo() {
        server.expect(requestTo("http://localhost:8080/api/lista/listar?nome=Centro&pagina=0&tamanho=5"))
                .andRespond(withSuccess("""
                        {
                          "conteudo": [{
                            "id": "b4d0a150-2d80-4f0b-9a1d-3cfb8d8a0f21",
                            "nome": "Lista Centro",
                            "descricao": "Restrição de circulação",
                            "totalRegras": 2,
                            "totalCidades": 3,
                            "arquivado": false,
                            "dataArquivado": null
                          }],
                          "paginaAtual": 0,
                          "tamanhoPagina": 5,
                          "totalElementos": 1,
                          "totalPaginas": 1
                        }
                        """, MediaType.APPLICATION_JSON));

        var page = api.listarAtivas("Centro", 0, 5);

        assertEquals(1, page.totalElementos());
        assertEquals(2, page.conteudo().getFirst().totalRegras());
        assertEquals(3, page.conteudo().getFirst().totalCidades());
        assertFalse(page.conteudo().getFirst().arquivado());
    }

    @Test
    void salvarEnviaDadosParaApi() {
        var id = UUID.fromString("b4d0a150-2d80-4f0b-9a1d-3cfb8d8a0f21");
        server.expect(requestTo("http://localhost:8080/api/lista"))
                .andExpect(content().json("""
                        {"nome":"Lista Centro","descricao":"Restrição de circulação"}
                        """))
                .andRespond(withCreatedEntity(URI.create("/api/lista/" + id)));

        assertEquals(id, api.salvar(new ListaRequestDto("Lista Centro", "Restrição de circulação")));
    }

    @Test
    void salvarAtualizaTokenERepeteRequisicaoQuandoApiRetornaUnauthorized() {
        var id = UUID.fromString("b4d0a150-2d80-4f0b-9a1d-3cfb8d8a0f21");
        server.expect(requestTo("http://localhost:8080/api/lista"))
                .andRespond(withStatus(HttpStatus.UNAUTHORIZED));
        server.expect(requestTo("http://localhost:8080/api/lista"))
                .andRespond(withCreatedEntity(URI.create("/api/lista/" + id)));
        var loginApplication = mock(LoginApplicationPorta.class);
        api = new ListaApi(
                new RestClientProtegido(clientBuilder.build()),
                new RetryComRefresh(loginApplication));

        assertEquals(id, api.salvar(new ListaRequestDto("Lista Centro", "Restrição de circulação")));
        verify(loginApplication).refresh();
    }

    @Test
    void atualizarRegrasEnviaRegrasDeCadastroESeparacao() {
        var id = UUID.fromString("b4d0a150-2d80-4f0b-9a1d-3cfb8d8a0f21");
        var idVeiculo = UUID.fromString("d4d0a150-2d80-4f0b-9a1d-3cfb8d8a0f22");
        var idCidade = UUID.fromString("e4d0a150-2d80-4f0b-9a1d-3cfb8d8a0f23");
        var regras = new ListaRegrasRequestDto(
                List.of(new ListaRegrasRequestDto.RegraCadastro(
                        List.of(idVeiculo),
                        List.of(idCidade),
                        List.of("ALTA"),
                        "08:00",
                        "12:00",
                        true,
                        "Atendimento prioritário")),
                List.of(new ListaRegrasRequestDto.RegraSeparacao(idVeiculo, 5_400, 0)));
        server.expect(requestTo("http://localhost:8080/api/lista/" + id + "/regras"))
                .andExpect(content().json("""
                        {
                          "regrasCadastro":[{
                            "idsVeiculos":["%s"],
                            "idsCidades":["%s"],
                            "prioridades":["ALTA"],
                            "inicioHorario":"08:00",
                            "finalHorario":"12:00",
                            "obrigacaoAnexo":true,
                            "descricao":"Atendimento prioritário"
                          }],
                          "regrasSeparacao":[{
                            "idTipoVeiculo":"%s",
                            "tempoToleranciaSegundos":5400,
                            "tempoToleranciaNanos":0
                          }]
                        }
                        """.formatted(idVeiculo, idCidade, idVeiculo)))
                .andRespond(withStatus(HttpStatus.NO_CONTENT));

        api.atualizarRegras(id, regras);
    }
}
