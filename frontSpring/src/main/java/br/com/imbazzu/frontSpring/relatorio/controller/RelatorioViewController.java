package br.com.imbazzu.frontSpring.relatorio.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/relatorios")
public class RelatorioViewController {

    @GetMapping
    public String exibirRelatorios(Model model) {
        model.addAttribute("pagina", "relatorios");
        return "relatorio/relatorios";
    }
}
