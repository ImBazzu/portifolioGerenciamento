package br.com.imbazzu.frontSpring.relatorio.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/relatorios")
public class RelatorioViewController {

    @GetMapping
    public String exibirRelatorios() {
        return "relatorio/relatorios";
    }
}
