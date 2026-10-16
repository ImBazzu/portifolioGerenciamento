package br.com.imbazzu.frontSpring.veiculo.adpter.in.controller;

import br.com.imbazzu.frontSpring.controller.dto.ResultadoPaginado;
import br.com.imbazzu.frontSpring.veiculo.core.dto.TipoVeiculoResponseDto;
import br.com.imbazzu.frontSpring.veiculo.core.porta.in.VeiculoApplicationPorta;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxTriggerAfterSettle;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("veiculo")
public class VeiculoController {

    private final VeiculoApplicationPorta application;

    public VeiculoController(VeiculoApplicationPorta application){
        this.application = application;
    }

    @GetMapping
    public String inicio(Model model) {
        model.addAttribute("pagina", "veiculos");
        return "veiculo/inicio";
    }

    @GetMapping("/listar")
    public String listarVeiculos(
            @RequestParam(required = false, defaultValue = "") String busca,
            @RequestParam(defaultValue = "0") int pagina,
            Model model) {

        ResultadoPaginado<TipoVeiculoResponseDto> paginaVeiculos = application.listar(busca, pagina);
        model.addAttribute("veiculos", paginaVeiculos.conteudo());
        model.addAttribute("paginaAtual", pagina);
        model.addAttribute("totalPaginas", paginaVeiculos.totalPaginas());
        model.addAttribute("busca", busca);

        return "veiculo/tabVeiculosAtivos :: tabVeiculosAtivos";
    }

    @PatchMapping("/{id}/arquivar")
    @HxRequest
    @HxTriggerAfterSettle({"listar-ativos","fechar-formulario"})
    @ResponseBody
    public void arquivarVeiculo(@PathVariable UUID id) {
        application.arquivar(id);
    }

    @GetMapping("/limpar-modal")
    @HxRequest
    public String limparModal() {
        return "layout/vazio::vazio";
    }




}
