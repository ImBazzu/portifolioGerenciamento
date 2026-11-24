package br.com.imbazzu.frontSpring.relatorio.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import br.com.imbazzu.frontSpring.autenticacao.core.porta.out.RetryComRefreshPorta;
import br.com.imbazzu.frontSpring.autenticacao.infraestrutura.httpConfig.RestClientProtegido;

@RestController
public class PassageirosPeriodoController {
    private final RestClient api;
    private final RetryComRefreshPorta retry;

    public PassageirosPeriodoController(RestClientProtegido api, RetryComRefreshPorta retry) {
        this.api = api.get();
        this.retry = retry;
    }

    @GetMapping("/relatorios/passageiros/opcoes")
    public ResponseEntity<Opcoes> opcoes() {
        return ResponseEntity.ok().cacheControl(org.springframework.http.CacheControl.noStore())
                .body(consultarOpcoes());
    }

    @GetMapping(value = "/relatorios/passageiros/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> pdf(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(required = false) List<UUID> listas,
            @RequestParam(required = false) UUID cidade,
            @RequestParam(required = false) UUID destino,
            @RequestParam(required = false) UUID tipoVeiculo) {
        var todas = consultar(dataInicio, dataFim);
        var opcoes = consultarOpcoes();
        var selecionadas = todas.stream()
                .filter(v -> listas == null || listas.isEmpty() || listas.contains(v.idLista()))
                .filter(v -> cidade == null || cidade.equals(v.idCidade()))
                .filter(v -> destino == null || destino.equals(v.idDestino()))
                .filter(v -> tipoVeiculo == null || tipoVeiculo.equals(v.idTipoVeiculo()))
                .toList();
        var filtros = List.of(
                "Listas: " + nomes(opcoes.listas(), listas),
                "Cidade: " + nomes(opcoes.cidades(), cidade == null ? null : List.of(cidade)),
                "Destino: " + nomes(opcoes.destinos().stream().map(o -> new Opcao(o.id(), o.nome())).toList(), destino == null ? null : List.of(destino)),
                "Tipo de veículo: " + nomes(opcoes.tiposVeiculo(), tipoVeiculo == null ? null : List.of(tipoVeiculo)));
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"passageiros-" + dataInicio + "-a-" + dataFim + ".pdf\"")
                .body(PassageirosPeriodoPdf.gerar(dataInicio, dataFim, filtros, selecionadas));
    }

    private Opcoes consultarOpcoes() {
        var resultado = retry.executar(() -> api.get().uri("/relatorios/passageiros-opcoes")
                .retrieve().body(Opcoes.class));
        if (resultado == null) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "A API não retornou os filtros.");
        }
        return resultado;
    }

    private List<PassageiroPeriodo> consultar(LocalDate inicio, LocalDate fim) {
        if (inicio.isAfter(fim)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Data inicial posterior à data final");
        }
        var resultado = retry.executar(() -> api.get().uri(builder -> builder.path("/relatorios/passageiros-periodo")
                .queryParam("dataInicio", inicio).queryParam("dataFim", fim).build())
                .retrieve().body(new ParameterizedTypeReference<List<PassageiroPeriodo>>() {}));
        if (resultado == null) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "A API não retornou os agendamentos.");
        }
        return resultado;
    }

    private static String nomes(List<Opcao> opcoes, List<UUID> selecionados) {
        if (selecionados == null || selecionados.isEmpty()) return "Todos";
        return selecionados.stream().map(valor -> opcoes.stream().filter(o -> o.id().equals(valor))
                .map(Opcao::nome).findFirst().orElse(valor.toString())).distinct()
                .collect(java.util.stream.Collectors.joining(", "));
    }

    public record Opcao(UUID id, String nome) {}
    public record Destino(UUID id, String nome, UUID idCidade) {}
    public record Opcoes(List<Opcao> listas, List<Opcao> cidades, List<Destino> destinos, List<Opcao> tiposVeiculo) {}
}
