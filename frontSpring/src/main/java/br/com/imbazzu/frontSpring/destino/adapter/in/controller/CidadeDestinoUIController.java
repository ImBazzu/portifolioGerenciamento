package br.com.imbazzu.frontspring.destino.adapter.in.controller;

import br.com.imbazzu.frontspring.destino.cidadedestino.CidadeDestinoFrontService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import br.com.imbazzu.frontspring.destino.cidadedestino.dto.CidadeDestinoRequestDto;

import java.util.UUID;

@Controller
@RequestMapping("/cidade")
@RequiredArgsConstructor
public class CidadeDestinoUIController {

    private final CidadeDestinoFrontService cidadeDestinoFrontService;

    @GetMapping
    public String inicio(Model model){
        model.addAttribute("pagina", "cidades");
        return "cidade/inicio";
    }


//
//    @GetMapping
//    public String paginaListagem(@RequestParam(defaultValue = "") String nome,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "20") int size,
//            Model model) {
//
//        var resultado = cidadeDestinoFrontService.buscarPorNome(nome, page, size);
//        model.addAttribute("cidades", resultado.conteudo());
//        model.addAttribute("uri", "/cidade-destino");
//
//        return "paginas/cidadedestino/cidadedestino";
//    }

    @GetMapping("/tabela")
    public String recarregarTabela(@RequestParam(defaultValue = "") String nome,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Model model) {

        var resultado = cidadeDestinoFrontService.buscarPorNome(nome, page, size);
        model.addAttribute("cidades", resultado.conteudo());

        return "paginas/cidadedestino/cidadedestino :: #tabela-cidades-container";
    }

    @GetMapping("/form")
    public String abrirModalNovo(Model model) {
        return "paginas/cidadedestino/form-cidadedestino :: form";
    }

    @GetMapping("/form/{id}")
    public String abrirModalEditar(@PathVariable UUID id, Model model) {
        var cidade = cidadeDestinoFrontService.buscarPorId(id);
        model.addAttribute("cidadeDto", cidade);
        return "paginas/cidadedestino/form-cidadedestino :: form";
    }

    @PostMapping
    public String salvarCidade(@ModelAttribute CidadeDestinoRequestDto cidadeDto,
            @RequestParam(defaultValue = "") String nomeFiltro,
            Model model) {
        cidadeDestinoFrontService.salvar(cidadeDto);

        var resultado = cidadeDestinoFrontService.buscarPorNome(nomeFiltro, 0, 20);
        model.addAttribute("cidades", resultado.conteudo());

        return "paginas/cidadedestino/cidadedestino :: #tabela-cidades-container";
    }

    @PostMapping("/editar/{id}")
    public String editarCidade(@PathVariable UUID id, @ModelAttribute CidadeDestinoRequestDto cidadeDto,
            @RequestParam(defaultValue = "") String nomeFiltro,
            Model model) {
        cidadeDestinoFrontService.editar(id, cidadeDto);

        var resultado = cidadeDestinoFrontService.buscarPorNome(nomeFiltro, 0, 20);
        model.addAttribute("cidades", resultado.conteudo());

        return "paginas/cidadedestino/cidadedestino :: #tabela-cidades-container";
    }

    @PatchMapping("/{id}/arquivar")
    @ResponseBody
    public String arquivarCidade(@PathVariable UUID id) {
        cidadeDestinoFrontService.arquivar(id);
        return "";
    }
}