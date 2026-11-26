package br.com.imbazzu.frontSpring.relatorio.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriBuilder;

import br.com.imbazzu.frontSpring.autenticacao.core.porta.out.RetryComRefreshPorta;
import br.com.imbazzu.frontSpring.autenticacao.infraestrutura.httpConfig.RestClientProtegido;

@Service
public class InvestigacaoApi {
    public record Pagina<T>(List<T> content, long totalElements, int totalPages, int number) {}
    public record Pessoa(UUID id, String nome, String cpf, String telefone, String especificacao, LocalDate arquivadaEm) {}
    public record Agendamento(UUID id, LocalDate data, LocalTime chegada, String cidade, String destino,
            String lista, String tipoVeiculo, String identificacao, String motorista, UUID veiculoId,
            int acompanhantes, boolean buscarNaResidencia, String autor) {}
    public record PessoaDetalhe(Pessoa pessoa, Pagina<Agendamento> agendamentos) {}
    public record Motorista(UUID id, String nome, String categoria, long veiculos, long passageiros) {}
    public record Resumo(long motoristas, long veiculos, long passageiros) {}
    public record Passageiro(String nome, String cidade, String destino, LocalTime chegada, int acompanhantes) {}
    public record Veiculo(UUID id, LocalDate data, String motorista, String categoria, String tipo,
            String identificacao, LocalTime saida, boolean saidaCalculada, List<Passageiro> passageiros) {}
    public record Motoristas(Resumo resumo, Pagina<Motorista> porMotorista, Pagina<Veiculo> veiculos) {}
    public record Filtros(LocalDate inicio, LocalDate fim, String cidade, String destino) {}

    private final RestClient api;
    private final RetryComRefreshPorta retry;

    public InvestigacaoApi(RestClientProtegido api, RetryComRefreshPorta retry) {
        this.api = api.get();
        this.retry = retry;
    }

    public Pagina<Pessoa> pessoas(String nome, int pagina) {
        var resultado = retry.executar(() -> api.get().uri(uri -> uri.path("/relatorios/investigacao/pessoas")
                .queryParam("nome", "{nome}").queryParam("pagina", pagina).build(Map.of("nome", nome)))
                .retrieve().body(new ParameterizedTypeReference<Pagina<Pessoa>>() {}));
        return exigir(resultado);
    }

    public PessoaDetalhe pessoa(UUID id, Filtros filtros, String situacao, int pagina) {
        var resultado = retry.executar(() -> api.get().uri(uri -> filtros(uri.path("/relatorios/investigacao/pessoas/{id}"), filtros)
                .queryParam("situacao", situacao).queryParam("pagina", pagina)
                .build(Map.of("id", id, "cidade", filtros.cidade(), "destino", filtros.destino())))
                .retrieve().body(PessoaDetalhe.class));
        return exigir(resultado);
    }

    public Motoristas motoristas(String nome, UUID motorista, String categoria, Filtros filtros, int pagina, int resumoPagina) {
        var resultado = retry.executar(() -> api.get().uri(uri -> {
            var builder = filtros(uri.path("/relatorios/investigacao/motoristas"), filtros);
            if (motorista != null) builder.queryParam("motorista", motorista);
            return builder
                .queryParam("nome", "{nome}").queryParam("categoria", "{categoria}")
                .queryParam("pagina", pagina).queryParam("resumoPagina", resumoPagina)
                .build(Map.of("nome", nome, "categoria", categoria, "cidade", filtros.cidade(), "destino", filtros.destino()));
        })
                .retrieve().body(Motoristas.class));
        return exigir(resultado);
    }

    private UriBuilder filtros(UriBuilder uri, Filtros filtros) {
        if (filtros.inicio() != null) uri.queryParam("inicio", filtros.inicio());
        if (filtros.fim() != null) uri.queryParam("fim", filtros.fim());
        return uri.queryParam("cidade", "{cidade}").queryParam("destino", "{destino}");
    }

    private static <T> T exigir(T resultado) {
        if (resultado == null) throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "A API não retornou o relatório.");
        return resultado;
    }
}
