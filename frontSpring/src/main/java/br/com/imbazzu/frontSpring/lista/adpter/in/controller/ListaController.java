package br.com.imbazzu.frontSpring.lista.adpter.in.controller;

import br.com.imbazzu.frontSpring.controller.dto.ResultadoPaginado;
import br.com.imbazzu.frontSpring.lista.core.dto.ListaResponseDto;
import br.com.imbazzu.frontSpring.lista.core.porta.in.ListaApplicationPorta;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxTriggerAfterSettle;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.UUID;

@Controller
@RequestMapping("/listas")
public class ListaController {

    private final ListaApplicationPorta application;

    public ListaController(ListaApplicationPorta application) {
        this.application = application;
    }

    @GetMapping
    public String inicio(Model model) {
        model.addAttribute("pagina", "listas");
        return "lista/inicio";
    }

    @HxRequest
    @GetMapping("/listar")
    public String listar(
            @RequestParam(required = false, defaultValue = "") String busca,
            @RequestParam(defaultValue = "0") int pagina,
            Model model) {
        ResultadoPaginado<ListaResponseDto> resultado = application.listarAtivas(busca, pagina);
        model.addAttribute("listas", resultado.conteudo());
        model.addAttribute("paginaAtual", resultado.paginaAtual());
        model.addAttribute("totalPaginas", resultado.totalPaginas());
        model.addAttribute("busca", busca);
        return "lista/tabListasAtivas :: tabListasAtivas";
    }

    @HxRequest
    @GetMapping("/listarArquivadas")
    public String listarArquivadas(
            @RequestParam(required = false, defaultValue = "") String busca,
            @RequestParam(defaultValue = "0") int pagina,
            Model model) {
        ResultadoPaginado<ListaResponseDto> resultado = application.listarArquivadas(busca, pagina);
        model.addAttribute("listasArquivadas", resultado.conteudo());
        model.addAttribute("paginaAtualArquivadas", resultado.paginaAtual());
        model.addAttribute("totalPaginasArquivadas", resultado.totalPaginas());
        model.addAttribute("busca", busca);
        return "lista/tabListasArquivadas :: tabListasArquivadas";
    }

    @HxRequest
    @GetMapping("/{id}/detalhes")
    public String detalhes(@PathVariable UUID id, Model model) {
        model.addAttribute("lista", application.buscarAtiva(id));
        return "lista/detalhesLista :: detalhesLista";
    }

    @HxRequest
    @PatchMapping("/{id}/arquivar")
    @HxTriggerAfterSettle("listar-ativos")
    @ResponseBody
    public void arquivar(@PathVariable UUID id) {
        application.arquivar(id);
    }

    @HxRequest
    @PatchMapping("/{id}/desarquivar")
    @HxTriggerAfterSettle("listar-arquivadas")
    @ResponseBody
    public void desarquivar(@PathVariable UUID id) {
        application.desarquivar(id);
    }

    @HxRequest
    @GetMapping("/limpar-modal")
    public String limparModal() {
        return "layout/vazio :: vazio";
    }
}
