package br.com.imbazzu.frontspring.veiculo.adpter.in.controller;

import br.com.imbazzu.frontspring.veiculo.core.porta.in.VeiculoApplicationPorta;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxTriggerAfterSettle;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/veiculo")
@RequiredArgsConstructor
public class VeiculoArquivadoController {

    private final VeiculoApplicationPorta application;

    @GetMapping("/listarArquivado")
    @HxRequest
    public String listarVeiculosArquivados(
            @RequestParam(required = false, defaultValue = "") String busca,
            @RequestParam(defaultValue = "0") int pagina,
            Model model) {

        var paginaArquivados = application.listarArquivados(busca, pagina);

        model.addAttribute("veiculosArquivados", paginaArquivados.conteudo());
        model.addAttribute("paginaAtualArquivados", pagina);
        model.addAttribute("totalPaginasArquivados", paginaArquivados.totalPaginas());
        model.addAttribute("busca", busca);

        return "veiculo/tabVeiculosArquivados :: tabVeiculosArquivados";
    }


    @PatchMapping("/{id}/desarquivar")
    @HxRequest
    @HxTriggerAfterSettle({"listar-ativos","listar-arquivados"})
    @ResponseBody
    public void desarquivarVeiculo(@PathVariable UUID id) {
        application.desarquivar(id);
    }


}
