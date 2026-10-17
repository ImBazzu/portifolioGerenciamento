package br.com.imbazzu.frontSpring.destino.adapter.in.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/cidade")
public class CidadeDestinoUIController {

    @GetMapping
    public String redirecionar() {
        return "redirect:/localidade";
    }
}