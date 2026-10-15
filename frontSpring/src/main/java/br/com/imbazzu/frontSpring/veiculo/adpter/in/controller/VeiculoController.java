package br.com.imbazzu.frontspring.veiculo.adpter.in.controller;

import br.com.imbazzu.frontspring.controller.dto.ResultadoPaginado;
import br.com.imbazzu.frontspring.veiculo.core.dto.TipoVeiculoArquivadoResponseDto;
import br.com.imbazzu.frontspring.veiculo.core.dto.TipoVeiculoHistoricoResponseDto;
import br.com.imbazzu.frontspring.veiculo.core.dto.TipoVeiculoRequestDto;
import br.com.imbazzu.frontspring.veiculo.core.dto.TipoVeiculoResponseDto;
import br.com.imbazzu.frontspring.veiculo.core.excecoes.VeiculoDuplicadoException;
import br.com.imbazzu.frontspring.veiculo.core.excecoes.VeiculoNaoEncontradoException;
import br.com.imbazzu.frontspring.veiculo.core.porta.in.VeiculoApplicationPorta;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxResponse;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxTriggerAfterSettle;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("veiculo")
@RequiredArgsConstructor
public class VeiculoController {

    private final VeiculoApplicationPorta application;

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
