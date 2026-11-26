package br.com.imbazzu.frontSpring.relatorio.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/relatorios/investigacao")
public class InvestigacaoPaginaController {
    private final InvestigacaoApi api;

    public InvestigacaoPaginaController(InvestigacaoApi api) {
        this.api = api;
    }

    @GetMapping("/pessoas")
    public String pessoas(@RequestParam(defaultValue = "") String nome,
            @RequestParam(required = false) UUID pessoa,
            @RequestParam(required = false) LocalDate inicio, @RequestParam(required = false) LocalDate fim,
            @RequestParam(defaultValue = "") String cidade, @RequestParam(defaultValue = "") String destino,
            @RequestParam(defaultValue = "TODOS") String situacao,
            @RequestParam(defaultValue = "0") int pagina, @RequestParam(defaultValue = "0") int pessoaPagina,
            Model model) {
        if (!List.of("TODOS", "ALOCADO", "PENDENTE").contains(situacao)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Situação inválida");
        }
        preencher(model, inicio, fim, cidade, destino);
        model.addAttribute("nome", nome);
        model.addAttribute("pessoaId", pessoa);
        model.addAttribute("situacao", situacao);
        if (valido(model, inicio, fim)) {
            model.addAttribute("pessoas", api.pessoas(nome, pessoaPagina));
            if (pessoa != null) {
                model.addAttribute("detalhe", api.pessoa(pessoa, new InvestigacaoApi.Filtros(inicio, fim, cidade, destino), situacao, pagina));
            }
        }
        return "relatorio/investigacao-pessoas";
    }

    @GetMapping("/motoristas")
    public String motoristas(@RequestParam(defaultValue = "") String nome,
            @RequestParam(required = false) UUID motorista,
            @RequestParam(defaultValue = "") String categoria,
            @RequestParam(required = false) LocalDate inicio, @RequestParam(required = false) LocalDate fim,
            @RequestParam(defaultValue = "") String cidade, @RequestParam(defaultValue = "") String destino,
            @RequestParam(defaultValue = "0") int pagina, @RequestParam(defaultValue = "0") int resumoPagina,
            @RequestParam(defaultValue = "0") int cidadePagina,
            Model model) {
        preencher(model, inicio, fim, cidade, destino);
        model.addAttribute("nome", nome);
        model.addAttribute("motoristaId", motorista);
        model.addAttribute("categoria", categoria);
        if (valido(model, inicio, fim)) {
            model.addAttribute("resultado", api.motoristas(nome, motorista, categoria,
                    new InvestigacaoApi.Filtros(inicio, fim, cidade, destino), pagina, resumoPagina, cidadePagina));
        }
        return "relatorio/investigacao-motoristas";
    }

    private void preencher(Model model, LocalDate inicio, LocalDate fim, String cidade, String destino) {
        model.addAttribute("pagina", "relatorios");
        model.addAttribute("inicio", inicio);
        model.addAttribute("fim", fim);
        model.addAttribute("cidade", cidade);
        model.addAttribute("destino", destino);
    }

    private boolean valido(Model model, LocalDate inicio, LocalDate fim) {
        if (inicio != null && fim != null && inicio.isAfter(fim)) {
            model.addAttribute("erro", "A data inicial não pode ser posterior à data final.");
            return false;
        }
        return true;
    }
}
