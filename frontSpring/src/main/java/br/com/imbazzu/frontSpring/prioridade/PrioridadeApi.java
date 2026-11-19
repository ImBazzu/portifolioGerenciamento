package br.com.imbazzu.frontSpring.prioridade;

import java.util.List;
import java.util.UUID;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.annotation.JsonProperty;

import br.com.imbazzu.frontSpring.autenticacao.core.porta.out.RetryComRefreshPorta;
import br.com.imbazzu.frontSpring.autenticacao.infraestrutura.httpConfig.RestClientProtegido;

@Service
public class PrioridadeApi {
    public record Prioridade(UUID id, String nome, boolean buscarNaResidencia, int quantidadeAcompanhantes,
                             boolean arquivada,
                             @JsonProperty(value = "quantidadeEspecificacoes", required = true)
                             long quantidadeEspecificacoes) {}
    public record Dados(String nome, boolean buscarNaResidencia, int quantidadeAcompanhantes) {}

    private final RestClient api;
    private final RetryComRefreshPorta retry;

    public PrioridadeApi(RestClientProtegido api, RetryComRefreshPorta retry) {
        this.api = api.get();
        this.retry = retry;
    }

    public List<Prioridade> listar() {
        return retry.executar(() -> api.get().uri("/prioridades").retrieve()
                .body(new ParameterizedTypeReference<List<Prioridade>>() {}));
    }

    public Prioridade buscar(UUID id) {
        return retry.executar(() -> api.get().uri("/prioridades/{id}", id).retrieve().body(Prioridade.class));
    }

    public void criar(Dados dados) {
        retry.executar(() -> api.post().uri("/prioridades").body(dados).retrieve().toBodilessEntity());
    }

    public void editar(UUID id, Dados dados) {
        retry.executar(() -> api.put().uri("/prioridades/{id}", id).body(dados).retrieve().toBodilessEntity());
    }

    public void alterarStatus(UUID id, boolean arquivar) {
        String acao = arquivar ? "arquivar" : "desarquivar";
        retry.executar(() -> api.patch().uri("/prioridades/{id}/{acao}", id, acao).retrieve().toBodilessEntity());
    }

    public void excluir(UUID id) {
        retry.executar(() -> api.delete().uri("/prioridades/{id}", id).retrieve().toBodilessEntity());
    }
}
