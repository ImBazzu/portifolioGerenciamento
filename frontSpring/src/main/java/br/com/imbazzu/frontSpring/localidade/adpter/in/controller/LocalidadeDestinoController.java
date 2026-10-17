package br.com.imbazzu.frontSpring.localidade.adpter.in.controller;

import br.com.imbazzu.frontSpring.localidade.core.dto.CidadeResponseDto;
import br.com.imbazzu.frontSpring.localidade.core.dto.DestinoRequestDto;
import br.com.imbazzu.frontSpring.localidade.core.dto.DestinoResponseDto;
import br.com.imbazzu.frontSpring.localidade.core.excecoes.LocalidadeDuplicadaException;
import br.com.imbazzu.frontSpring.localidade.core.excecoes.LocalidadeNaoEncontradaException;
import br.com.imbazzu.frontSpring.localidade.core.porta.in.LocalidadeApplicationPorta;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxTriggerAfterSettle;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/localidade")
public class LocalidadeDestinoController {

    private final LocalidadeApplicationPorta application;

    public LocalidadeDestinoController(LocalidadeApplicationPorta application) {
        this.application = application;
    }

    @HxRequest
    @GetMapping("/{idCidade}/destinos")
    public String listar(
            @PathVariable UUID idCidade,
            @RequestParam(defaultValue = "") String busca,
            @RequestParam(defaultValue = "0") int pagina,
            Model model) {
        var resultado = application.listarDestinos(idCidade, busca, pagina);
        model.addAttribute("destinos", resultado.conteudo());
        model.addAttribute("idCidade", idCidade);
        model.addAttribute("paginaAtual", pagina);
        model.addAttribute("totalPaginas", resultado.totalPaginas());
        model.addAttribute("busca", busca);
        return "localidade/tabDestinosAtivos :: tabDestinosAtivos";
    }

    @HxRequest
    @GetMapping("/{idCidade}/destinos/arquivados")
    public String listarArquivados(
            @PathVariable UUID idCidade,
            @RequestParam(defaultValue = "") String busca,
            @RequestParam(defaultValue = "0") int pagina,
            Model model) {
        var resultado = application.listarDestinosArquivados(idCidade, busca, pagina);
        model.addAttribute("destinosArquivados", resultado.conteudo());
        model.addAttribute("idCidade", idCidade);
        model.addAttribute("paginaAtual", pagina);
        model.addAttribute("totalPaginas", resultado.totalPaginas());
        model.addAttribute("busca", busca);
        return "localidade/tabDestinosArquivados :: tabDestinosArquivados";
    }

    @HxRequest
    @GetMapping("/{idCidade}/destinos/abrirFormulario")
    public String formNovo(@PathVariable UUID idCidade, Model model) {
        prepararFormulario(model, new DestinoRequestDto(idCidade, ""), null);
        return "localidade/formDestino :: formDestino";
    }

    @HxRequest
    @GetMapping("/destinos/abrirFormulario/{id}")
    public String formEditar(
            @PathVariable UUID id,
            @RequestParam UUID idCidade,
            @RequestParam(defaultValue = "false") boolean arquivado,
            Model model) {
        try {
            DestinoResponseDto destino = arquivado
                    ? application.buscarDestinoArquivado(id)
                    : application.buscarDestino(id);
            prepararFormulario(model, new DestinoRequestDto(idCidade, destino.nome()), id, idCidade);
            model.addAttribute("arquivado", arquivado);
        } catch (LocalidadeNaoEncontradaException e) {
            prepararFormulario(model, new DestinoRequestDto(idCidade, ""), id, idCidade);
            model.addAttribute("arquivado", arquivado);
            model.addAttribute("erroGlobal", "Destino não encontrado ou removido.");
        }
        return "localidade/formDestino :: formDestino";
    }

    @HxRequest
    @PostMapping("/destinos")
    @HxTriggerAfterSettle("listar-ativos")
    public String salvar(
            @Valid @ModelAttribute("destino") DestinoRequestDto destino,
            BindingResult result,
            Model model) {
        prepararFormulario(model, destino, null, destino.idCidade());
        if (result.hasErrors()) {
            return "localidade/formDestino :: formDestino";
        }
        try {
            application.salvarDestino(destino);
            return detalhesCidade(destino.idCidade(), model);
        } catch (LocalidadeDuplicadaException e) {
            result.rejectValue("nome", "duplicado", "Já existe um destino com este nome nesta cidade.");
            return "localidade/formDestino :: formDestino";
        }
    }

    @HxRequest
    @PutMapping("/destinos/{id}")
    @HxTriggerAfterSettle("listar-ativos")
    public String editar(
            @PathVariable UUID id,
            @RequestParam UUID idCidade,
            @Valid @ModelAttribute("destino") DestinoRequestDto destino,
            BindingResult result,
            Model model) {
        prepararFormulario(model, destino, id, idCidade);
        if (result.hasErrors()) {
            return "localidade/formDestino :: formDestino";
        }
        try {
            application.editarDestino(id, destino.nome());
            return detalhesCidade(idCidade, model);
        } catch (LocalidadeDuplicadaException e) {
            result.rejectValue("nome", "duplicado", "Já existe um destino com este nome nesta cidade.");
            return "localidade/formDestino :: formDestino";
        } catch (LocalidadeNaoEncontradaException e) {
            result.reject("naoEncontrado", "O destino não foi encontrado ou foi removido.");
            model.addAttribute("erroGlobal", "O destino não foi encontrado ou foi removido.");
            return "localidade/formDestino :: formDestino";
        }
    }

    @HxRequest
    @PatchMapping("/destinos/arquivar/{id}")
    @HxTriggerAfterSettle({"listar-destinos", "listar-destinos-arquivados"})
    @ResponseBody
    public void arquivar(@PathVariable UUID id) {
        application.arquivarDestino(id);
    }

    @HxRequest
    @PatchMapping("/destinos/desarquivar/{id}")
    @HxTriggerAfterSettle({"listar-destinos", "listar-destinos-arquivados"})
    @ResponseBody
    public void desarquivar(@PathVariable UUID id) {
        application.desarquivarDestino(id);
    }

    private void prepararFormulario(Model model, DestinoRequestDto destino, UUID id) {
        prepararFormulario(model, destino, id, destino.idCidade());
    }

    private void prepararFormulario(Model model, DestinoRequestDto destino, UUID id, UUID idCidade) {
        model.addAttribute("destino", destino);
        model.addAttribute("id", id);
        model.addAttribute("idCidade", idCidade);
        model.addAttribute("arquivado", false);
    }

    private String detalhesCidade(UUID idCidade, Model model) {
        CidadeResponseDto cidade = application.buscarCidade(idCidade);
        model.addAttribute("cidade", cidade);
        model.addAttribute("arquivado", false);
        return "localidade/detalhesCidade :: detalhesCidade";
    }
}
