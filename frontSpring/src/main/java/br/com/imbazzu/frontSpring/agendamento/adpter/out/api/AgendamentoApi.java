package br.com.imbazzu.frontSpring.agendamento.adpter.out.api;

import java.time.LocalDate;
import java.util.UUID;
import java.util.function.Supplier;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.core.ParameterizedTypeReference;

import br.com.imbazzu.frontSpring.agendamento.core.dto.AbrirVeiculoRequestDto;
import br.com.imbazzu.frontSpring.agendamento.core.dto.AgendamentoRequestDto;
import br.com.imbazzu.frontSpring.agendamento.core.dto.AgendamentoResponseDto;
import br.com.imbazzu.frontSpring.agendamento.core.dto.AgendamentoAtualizarRequestDto;
import br.com.imbazzu.frontSpring.agendamento.core.dto.AgendaDiaResponseDto;
import br.com.imbazzu.frontSpring.agendamento.core.dto.ResultadoSeparacaoResponseDto;
import br.com.imbazzu.frontSpring.agendamento.core.dto.VerificarVeiculosRequestDto;
import br.com.imbazzu.frontSpring.agendamento.core.dto.VerificacaoVeiculosResponseDto;
import br.com.imbazzu.frontSpring.agendamento.core.dto.VeiculoAbertoAtualizarRequestDto;
import br.com.imbazzu.frontSpring.agendamento.core.dto.HistoricoViagemResponseDto;
import br.com.imbazzu.frontSpring.autenticacao.core.porta.out.RetryComRefreshPorta;
import br.com.imbazzu.frontSpring.autenticacao.infraestrutura.httpConfig.RestClientProtegido;
import br.com.imbazzu.frontSpring.controller.dto.ResultadoPaginado;

@Service
public class AgendamentoApi {

    private static final String PATH = "/agendamentos";

    private final RestClient api;
    private final RetryComRefreshPorta retryComRefresh;

    public AgendamentoApi(RestClientProtegido api, RetryComRefreshPorta retryComRefresh) {
        this.api = api.get();
        this.retryComRefresh = retryComRefresh;
    }

    public void abrirDia(LocalDate data) {
        executar(() -> api.post()
                .uri(PATH + "/dias/{data}/abrir", data)
                .retrieve()
                .toBodilessEntity());
    }

    public AgendamentoResponseDto buscarPorId(UUID id){
        return executar(()->api.get()
                .uri("%s/%s".formatted(PATH,id))
                .retrieve()
                .body(AgendamentoResponseDto.class));
    }

    public void abrirVeiculo(LocalDate data, AbrirVeiculoRequestDto request) {
        executar(() -> api.post()
                .uri(PATH + "/dias/{data}/veiculos", data)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toBodilessEntity());
    }

    public void atualizarVeiculo(UUID id, VeiculoAbertoAtualizarRequestDto request) {
        executar(() -> api.put()
                .uri(PATH + "/veiculos/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toBodilessEntity());
    }

    public void removerVeiculo(UUID id) {
        executar(() -> api.delete()
                .uri(PATH + "/veiculos/{id}", id)
                .retrieve()
                .toBodilessEntity());
    }

    public VerificacaoVeiculosResponseDto verificarVeiculos(
            LocalDate data, VerificarVeiculosRequestDto request) {
        return executar(() -> api.post()
                .uri(PATH + "/dias/{data}/verificar-veiculos", data)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(VerificacaoVeiculosResponseDto.class));
    }

    public void criar(LocalDate data, AgendamentoRequestDto request) {
        executar(() -> api.post()
                .uri(PATH + "/dias/{data}/passageiros", data)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toBodilessEntity());
    }

    public void atualizar(UUID id, AgendamentoAtualizarRequestDto request) {
        executar(() -> api.put()
                .uri(PATH + "/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toBodilessEntity());
    }

    public AgendaDiaResponseDto buscarDia(LocalDate data) {
        return executar(() -> api.get()
                .uri(PATH + "/dias/{data}", data)
                .retrieve()
                .body(AgendaDiaResponseDto.class));
    }

    public ResultadoSeparacaoResponseDto separarAutomaticamente(LocalDate data) {
        return executar(() -> api.post()
                .uri(PATH + "/dias/{data}/separacao-automatica", data)
                .retrieve()
                .body(ResultadoSeparacaoResponseDto.class));
    }

    public void atribuir(UUID idAgendamento, UUID idVeiculo) {
        executar(() -> api.put()
                .uri(PATH + "/{id}/veiculo/{idVeiculo}", idAgendamento, idVeiculo)
                .retrieve()
                .toBodilessEntity());
    }

    public void removerDoVeiculo(UUID idAgendamento) {
        executar(() -> api.delete()
                .uri(PATH + "/{id}/veiculo", idAgendamento)
                .retrieve()
                .toBodilessEntity());
    }

    public void remover(UUID idAgendamento) {
        executar(() -> api.delete()
                .uri(PATH + "/{id}", idAgendamento)
                .retrieve()
                .toBodilessEntity());
    }

    public ResultadoPaginado<HistoricoViagemResponseDto> buscarHistoricoViagens(UUID idPessoa, int pagina) {
        return executar(() -> api.get()
                .uri(PATH + "/pessoas/{idPessoa}/historico-viagens?page={page}&size=10", idPessoa, pagina)
                .retrieve()
                .body(new org.springframework.core.ParameterizedTypeReference<ResultadoPaginado<HistoricoViagemResponseDto>>() {}));
    }

    public ResultadoPaginado<HistoricoViagemResponseDto> buscarHistoricoViagensComFiltro(
            UUID idPessoa, String destinoBusca, int pagina) {
        return executar(() -> api.get()
                .uri(PATH + "/pessoas/{idPessoa}/historico-viagens?destinoBusca={destinoBusca}&page={page}&size=10", 
                        idPessoa, destinoBusca, pagina)
                .retrieve()
                .body(new org.springframework.core.ParameterizedTypeReference<ResultadoPaginado<HistoricoViagemResponseDto>>() {}));
    }

    private <T> T executar(Supplier<T> chamadaApi) {
        return retryComRefresh.executar(chamadaApi);
    }
}
