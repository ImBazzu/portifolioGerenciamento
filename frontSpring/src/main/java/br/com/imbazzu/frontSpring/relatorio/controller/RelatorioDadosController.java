package br.com.imbazzu.frontSpring.relatorio.controller;

import java.time.LocalDate;
import java.util.Set;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClient;

import br.com.imbazzu.frontSpring.autenticacao.core.porta.out.RetryComRefreshPorta;
import br.com.imbazzu.frontSpring.autenticacao.infraestrutura.httpConfig.RestClientProtegido;

@RestController
public class RelatorioDadosController {
    private static final Set<String> TIPOS = Set.of("agendamentos", "pessoas", "veiculos", "operacional");

    private final RestClient api;
    private final RetryComRefreshPorta retry;

    public RelatorioDadosController(RestClientProtegido api, RetryComRefreshPorta retry) {
        this.api = api.get();
        this.retry = retry;
    }

    @GetMapping(value = "/api/relatorios/{tipo}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String consultar(@PathVariable String tipo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        if (!TIPOS.contains(tipo)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        if (dataInicio != null && dataFim != null && dataInicio.isAfter(dataFim)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Data inicial posterior à data final");
        }
        return retry.executar(() -> api.get().uri(builder -> {
            var uri = builder.path("/relatorios/{tipo}");
            if (dataInicio != null) uri.queryParam("dataInicio", dataInicio);
            if (dataFim != null) uri.queryParam("dataFim", dataFim);
            return uri.build(tipo);
        }).retrieve().body(String.class));
    }
}
