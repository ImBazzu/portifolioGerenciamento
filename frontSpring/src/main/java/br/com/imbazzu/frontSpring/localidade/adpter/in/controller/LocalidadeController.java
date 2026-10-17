package br.com.imbazzu.frontSpring.localidade.adpter.in.controller;

import br.com.imbazzu.frontSpring.controller.dto.ResultadoPaginado;
import br.com.imbazzu.frontSpring.localidade.core.dto.CidadeArquivadoResponseDto;
import br.com.imbazzu.frontSpring.localidade.core.dto.CidadeResponseDto;
import br.com.imbazzu.frontSpring.localidade.core.porta.in.LocalidadeApplicationPorta;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxTriggerAfterSettle;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/localidade")
public class LocalidadeController {

    private final LocalidadeApplicationPorta application;

    public LocalidadeController(LocalidadeApplicationPorta application) {
        this.application = application;
    }

    @GetMapping
    public String inicio(Model model) {
        model.addAttribute("pagina", "localidade");
        return "localidade/inicio";
    }

    @HxRequest
    @GetMapping("/listar")
    public String listar(
            @RequestParam(defaultValue = "") String busca,
            @RequestParam(defaultValue = "0") int pagina,
            Model model) {
        ResultadoPaginado<CidadeResponseDto> resultado = application.listarCidades(busca, pagina);
        model.addAttribute("cidades", resultado.conteudo());
        model.addAttribute("paginaAtual", pagina);
        model.addAttribute("totalPaginas", resultado.totalPaginas());
        model.addAttribute("busca", busca);
        return "localidade/tabCidadesAtivas :: tabCidadesAtivas";
    }

    @HxRequest
    @GetMapping("/listarArquivado")
    public String listarArquivadas(
            @RequestParam(defaultValue = "") String busca,
            @RequestParam(defaultValue = "0") int pagina,
            Model model) {
        ResultadoPaginado<CidadeArquivadoResponseDto> resultado =
                application.listarCidadesArquivadas(busca, pagina);
        model.addAttribute("cidadesArquivadas", resultado.conteudo());
        model.addAttribute("paginaAtualArquivadas", pagina);
        model.addAttribute("totalPaginasArquivadas", resultado.totalPaginas());
        model.addAttribute("busca", busca);
        return "localidade/tabCidadesArquivadas :: tabCidadesArquivadas";
    }

    @HxRequest
    @PatchMapping("/{id}/arquivar")
    @HxTriggerAfterSettle({"listar-ativos", "fechar-formulario"})
    @ResponseBody
    public void arquivar(@PathVariable UUID id) {
        application.arquivarCidade(id);
    }

    @HxRequest
    @PatchMapping("/{id}/desarquivar")
    @HxTriggerAfterSettle({"listar-ativos", "listar-arquivados"})
    @ResponseBody
    public void desarquivar(@PathVariable UUID id) {
        application.desarquivarCidade(id);
    }

    @HxRequest
    @DeleteMapping("/{id}")
    @HxTriggerAfterSettle({"listar-ativos", "listar-arquivados"})
    @ResponseBody
    public void deletar(@PathVariable UUID id) {
        application.deletarCidade(id);
    }

    @HxRequest
    @GetMapping("/limpar-modal")
    public String limparModal() {
        return "layout/vazio :: vazio";
    }
}
