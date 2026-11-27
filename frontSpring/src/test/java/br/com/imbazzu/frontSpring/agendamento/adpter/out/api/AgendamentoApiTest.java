package br.com.imbazzu.frontSpring.agendamento.adpter.out.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import br.com.imbazzu.frontSpring.agendamento.core.dto.VerificarVeiculosRequestDto;
import br.com.imbazzu.frontSpring.agendamento.core.dto.AbrirVeiculoRequestDto;
import br.com.imbazzu.frontSpring.agendamento.core.dto.AgendamentoAtualizarRequestDto;
import br.com.imbazzu.frontSpring.autenticacao.core.porta.out.RetryComRefreshPorta;
import br.com.imbazzu.frontSpring.autenticacao.infraestrutura.httpConfig.RestClientProtegido;

class AgendamentoApiTest {

    private MockRestServiceServer server;
    private AgendamentoApi api;

    @BeforeEach
    void configurar() {
        var builder = RestClient.builder().baseUrl("http://localhost:8080/api");
        server = MockRestServiceServer.bindTo(builder).build();
        api = new AgendamentoApi(new RestClientProtegido(builder.build()), executarSemRefresh());
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
    void buscaDiaComAutorDoAgendamento() {
        server.expect(requestTo("http://localhost:8080/api/agendamentos/dias/2026-11-02"))
                .andRespond(withSuccess("""
                        {"data":"2026-11-02","veiculos":[],"passageirosPendentes":[{
                          "id":"6b0d3800-2ca7-4e1f-bfb3-36f849104d83","data":"2026-11-02",
                          "buscarNaResidencia":false,"acompanhantes":0,"idsVeiculosCompativeis":[],
                          "autorNome":"Usuário responsável"}]}
                        """, MediaType.APPLICATION_JSON));
        var agenda = api.buscarDia(LocalDate.of(2026, 11, 2));
        assertEquals("Usuário responsável", agenda.passageirosPendentes().getFirst().autorNome());
        server.verify();
    }

    @Test
    void aberturaRetornaIdentificacaoDoVeiculoCriado() {
        var data = LocalDate.of(2026, 11, 2);
        var id = UUID.randomUUID();
        var tipo = UUID.randomUUID();
        server.expect(requestTo("http://localhost:8080/api/agendamentos/dias/2026-11-02/veiculos"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("""
                        {"id":"%s","data":"2026-11-02","idTipoVeiculo":"%s",
                         "nomeTipoVeiculo":"Carro","capacidade":4,"assentosOcupados":0,"passageiros":[]}
                        """.formatted(id, tipo), MediaType.APPLICATION_JSON));

        assertEquals(id, api.abrirVeiculo(data, new AbrirVeiculoRequestDto(tipo, null, null)).id());
        server.verify();
    }

    @Test
    void verificarVeiculosUsaContratoDoBackend() {
        var data = LocalDate.of(2026, 11, 2);
        var idPessoa = UUID.fromString("6b0d3800-2ca7-4e1f-bfb3-36f849104d83");
        var idLista = UUID.fromString("5836e253-4c5a-42fd-ae3f-4e11b7b76b7e");
        var idDestino = UUID.fromString("ea5bb91c-4faa-4dfa-85f7-96fcbf849497");
        var idTipo = UUID.fromString("7b17f241-4e6a-4bdf-b1f9-06fe8760bb11");
        var idRegra = UUID.fromString("c734a894-2b9e-4eac-a193-3c88a0f4e135");
        server.expect(requestTo("http://localhost:8080/api/agendamentos/dias/2026-11-02/verificar-veiculos"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json("""
                        {
                          "idPessoa":"6b0d3800-2ca7-4e1f-bfb3-36f849104d83",
                          "idLista":"5836e253-4c5a-42fd-ae3f-4e11b7b76b7e",
                          "idDestino":"ea5bb91c-4faa-4dfa-85f7-96fcbf849497",
                          "horarioChegada":"09:00:00",
                          "acompanhantes":1
                        }
                        """))
                .andRespond(withSuccess("""
                        {
                          "veiculosPermitidos":[{
                            "idTipoVeiculo":"7b17f241-4e6a-4bdf-b1f9-06fe8760bb11",
                            "nome":"Van",
                            "capacidade":8,
                            "idsRegrasAprovadas":["c734a894-2b9e-4eac-a193-3c88a0f4e135"]
                          }],
                          "motivosReprovacao":[]
                        }
                        """, MediaType.APPLICATION_JSON));

        var resultado = api.verificarVeiculos(data, new VerificarVeiculosRequestDto(
                idPessoa, idLista, idDestino, LocalTime.of(9, 0), 1));

        assertEquals(idTipo, resultado.veiculosPermitidos().getFirst().idTipoVeiculo());
        assertEquals(List.of(idRegra), resultado.veiculosPermitidos().getFirst().idsRegrasAprovadas());
        server.verify();
    }

    @Test
    void atualizarAgendamentoUsaContratoPutDoBackend() {
        var id = UUID.fromString("6b0d3800-2ca7-4e1f-bfb3-36f849104d83");
        var idTipo = UUID.fromString("7b17f241-4e6a-4bdf-b1f9-06fe8760bb11");
        var idDestino = UUID.fromString("ea5bb91c-4faa-4dfa-85f7-96fcbf849497");
        var idVeiculo = UUID.fromString("5836e253-4c5a-42fd-ae3f-4e11b7b76b7e");
        server.expect(requestTo("http://localhost:8080/api/agendamentos/" + id))
                .andExpect(method(HttpMethod.PUT))
                .andExpect(content().json("""
                        {
                          "idTipoVeiculo":"7b17f241-4e6a-4bdf-b1f9-06fe8760bb11",
                          "idDestino":"ea5bb91c-4faa-4dfa-85f7-96fcbf849497",
                          "horarioChegada":"10:00:00",
                          "buscarNaResidencia":true,
                          "acompanhantes":2,
                          "idVeiculo":"5836e253-4c5a-42fd-ae3f-4e11b7b76b7e"
                        }
                        """))
                .andRespond(withSuccess());

        api.atualizar(id, new AgendamentoAtualizarRequestDto(
                idTipo, idDestino, LocalTime.of(10, 0), true, 2, idVeiculo));

        server.verify();
    }

    @Test
    void atualizarVeiculoAbertoUsaContratoPutDoBackend() {
        var id = UUID.fromString("6b0d3800-2ca7-4e1f-bfb3-36f849104d83");
        server.expect(requestTo("http://localhost:8080/api/agendamentos/veiculos/" + id))
                .andExpect(method(HttpMethod.PUT))
                .andExpect(content().json("""
                        {
                          "identificacao":"Van 02",
                          "horarioSaida":"08:30:00"
                        }
                        """))
                .andRespond(withSuccess());

        api.atualizarVeiculo(
                id, new br.com.imbazzu.frontSpring.agendamento.core.dto.VeiculoAbertoAtualizarRequestDto(
                        "Van 02", LocalTime.of(8, 30)));

        server.verify();
    }

    @Test
    void confirmarSaidaDoVeiculoUsaContratoPostDoBackend() {
        var id = UUID.fromString("6b0d3800-2ca7-4e1f-bfb3-36f849104d83");
        server.expect(requestTo("http://localhost:8080/api/agendamentos/veiculos/" + id + "/confirmar-saida"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess());

        api.confirmarSaida(id);

        server.verify();
    }

    @Test
    void desfazerConfirmacaoUsaDeleteDoBackend() {
        var id = UUID.randomUUID();
        server.expect(requestTo("http://localhost:8080/api/agendamentos/veiculos/" + id + "/confirmar-saida"))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withSuccess());
        api.desfazerConfirmacaoSaida(id);
        server.verify();
    }

    @Test
    void excluirVeiculoAbertoUsaContratoDeleteDoBackend() {
        var id = UUID.fromString("6b0d3800-2ca7-4e1f-bfb3-36f849104d83");
        server.expect(requestTo("http://localhost:8080/api/agendamentos/veiculos/" + id))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withSuccess());

        api.removerVeiculo(id);

        server.verify();
    }

    @Test
    void cancelarAgendamentoEnviaMotivoAoBackend() {
        var id = UUID.fromString("6b0d3800-2ca7-4e1f-bfb3-36f849104d83");
        server.expect(requestTo("http://localhost:8080/api/agendamentos/" + id + "/cancelar"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json("""
                        {"motivo":"Passageiro solicitou"}
                        """))
                .andRespond(withSuccess());

        api.cancelar(id, "Passageiro solicitou");

        server.verify();
    }

    @Test
    void buscaHistoricoPaginadoComFiltroCodificado() {
        var id = UUID.fromString("6b0d3800-2ca7-4e1f-bfb3-36f849104d83");
        server.expect(requestTo("http://localhost:8080/api/agendamentos/pessoas/" + id
                        + "/historico-viagens?destinoBusca=S%C3%A3o%20Paulo&page=1&size=10"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        {"conteudo":[{"id":"6b0d3800-2ca7-4e1f-bfb3-36f849104d83",
                          "data":"2026-11-02","horarioChegada":"09:00:00","horarioSaidaNecessario":"08:00:00",
                          "destinoNome":"São Paulo","listaDescricao":"Consulta","tipoVeiculoNome":"Van",
                          "buscarNaResidencia":true,"acompanhantes":1}],
                         "paginaAtual":1,"tamanhoPagina":10,"totalElementos":11,"totalPaginas":2}
                        """, MediaType.APPLICATION_JSON));

        var resultado = api.buscarHistoricoViagensComFiltro(id, "São Paulo", 1);
        assertEquals(2, resultado.totalPaginas());
        assertEquals(LocalTime.of(8, 0), resultado.conteudo().getFirst().horarioSaidaNecessario());
        assertEquals("São Paulo", resultado.conteudo().getFirst().destinoNome());
        server.verify();
    }
}
