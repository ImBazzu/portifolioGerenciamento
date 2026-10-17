package br.com.imbazzu.frontSpring.localidade.adpter.in.controller;

import br.com.imbazzu.frontSpring.destino.cidadedestino.dto.CidadeEstadoEnum;
import br.com.imbazzu.frontSpring.localidade.core.dto.CidadeArquivadoResponseDto;
import br.com.imbazzu.frontSpring.localidade.core.dto.CidadeRequestDto;
import br.com.imbazzu.frontSpring.localidade.core.dto.CidadeResponseDto;
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
public class LocalidadeFormularioController {

    private final LocalidadeApplicationPorta application;

    public LocalidadeFormularioController(LocalidadeApplicationPorta application) {
        this.application = application;
    }

    @HxRequest
    @GetMapping("/abrirFormulario")
    public String formNovo(Model model) {
        prepararFormulario(model, new CidadeRequestDto("", "", 0, 0), null, false);
        return "localidade/formCidade :: formCidade";
    }

    @HxRequest
    @GetMapping("/abrirFormulario/{id}")
    public String formEditar(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "false") boolean arquivado,
            Model model) {
        try {
            CidadeRequestDto dto = arquivado
                    ? converter(application.buscarCidadeArquivada(id))
                    : converter(application.buscarCidade(id));
            prepararFormulario(model, dto, id, arquivado);
        } catch (LocalidadeNaoEncontradaException e) {
            prepararFormulario(model, new CidadeRequestDto("", "", 0, 0), id, arquivado);
            model.addAttribute("erroGlobal", "Cidade não encontrada ou removida.");
        }
        return "localidade/formCidade :: formCidade";
    }

    @HxRequest
    @PostMapping
    @HxTriggerAfterSettle("listar-ativos")
    public String salvar(
            @Valid @ModelAttribute("cidade") CidadeRequestDto cidade,
            BindingResult result,
            Model model) {
        prepararFormulario(model, cidade, null, false);
        if (result.hasErrors()) {
            return "localidade/formCidade :: formCidade";
        }
        try {
            application.salvarCidade(cidade);
            return "layout/vazio :: vazio";
        } catch (LocalidadeDuplicadaException e) {
            result.rejectValue("nome", "duplicado", "Já existe uma cidade com este nome.");
            return "localidade/formCidade :: formCidade";
        }
    }

    @HxRequest
    @PutMapping("/{id}")
    @HxTriggerAfterSettle({"listar-ativos", "listar-arquivados"})
    public String editar(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "false") boolean arquivado,
            @Valid @ModelAttribute("cidade") CidadeRequestDto cidade,
            BindingResult result,
            Model model) {
        prepararFormulario(model, cidade, id, arquivado);
        if (result.hasErrors()) {
            return "localidade/formCidade :: formCidade";
        }
        try {
            application.editarCidade(id, cidade);
            return detalhes(id, arquivado, model);
        } catch (LocalidadeDuplicadaException e) {
            result.rejectValue("nome", "duplicado", "Já existe uma cidade com este nome.");
            return "localidade/formCidade :: formCidade";
        } catch (LocalidadeNaoEncontradaException e) {
            result.reject("naoEncontrada", "A cidade não foi encontrada ou foi removida.");
            model.addAttribute("erroGlobal", "A cidade não foi encontrada ou foi removida.");
            return "localidade/formCidade :: formCidade";
        }
    }

    @HxRequest
    @GetMapping("/{id}/detalhes")
    public String detalhes(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "false") boolean arquivado,
            Model model) {
        Object cidade = arquivado
                ? application.buscarCidadeArquivada(id)
                : application.buscarCidade(id);
        model.addAttribute("cidade", cidade);
        model.addAttribute("arquivado", arquivado);
        return "localidade/detalhesCidade :: detalhesCidade";
    }

    private void prepararFormulario(Model model, CidadeRequestDto cidade, UUID id, boolean arquivado) {
        model.addAttribute("cidade", cidade);
        model.addAttribute("id", id);
        model.addAttribute("arquivado", arquivado);
        model.addAttribute("estados", CidadeEstadoEnum.values());
    }

    private CidadeRequestDto converter(CidadeResponseDto cidade) {
        int[] duracao = separarTempo(cidade.tempoViagem());
        return new CidadeRequestDto(cidade.nome(), cidade.estado(), duracao[0], duracao[1]);
    }

    private CidadeRequestDto converter(CidadeArquivadoResponseDto cidade) {
        int[] duracao = separarTempo(cidade.tempoViagem());
        return new CidadeRequestDto(cidade.nome(), cidade.estado(), duracao[0], duracao[1]);
    }

    private int[] separarTempo(String tempoViagem) {
        String[] partes = tempoViagem.split(":", -1);
        if (partes.length != 2) {
            throw new IllegalStateException("Formato de tempo de viagem inesperado: " + tempoViagem);
        }
        return new int[] {Integer.parseInt(partes[0]), Integer.parseInt(partes[1])};
    }
}
