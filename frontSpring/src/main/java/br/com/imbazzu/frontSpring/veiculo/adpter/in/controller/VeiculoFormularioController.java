package br.com.imbazzu.frontSpring.veiculo.adpter.in.controller;

import br.com.imbazzu.frontSpring.veiculo.core.dto.TipoVeiculoArquivadoResponseDto;
import br.com.imbazzu.frontSpring.veiculo.core.dto.TipoVeiculoHistoricoResponseDto;
import br.com.imbazzu.frontSpring.veiculo.core.dto.TipoVeiculoRequestDto;
import br.com.imbazzu.frontSpring.veiculo.core.dto.TipoVeiculoResponseDto;
import br.com.imbazzu.frontSpring.veiculo.core.excecoes.VeiculoDuplicadoException;
import br.com.imbazzu.frontSpring.veiculo.core.excecoes.VeiculoNaoEncontradoException;
import br.com.imbazzu.frontSpring.veiculo.core.porta.in.VeiculoApplicationPorta;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxTriggerAfterSettle;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("veiculo")
public class VeiculoFormularioController {

    private final VeiculoApplicationPorta application;

    public VeiculoFormularioController(VeiculoApplicationPorta application){
        this.application = application;
    }

    @HxRequest
    @GetMapping("/abrirFormulario")
    public String formNovo(Model model) {
        var veiculo = new TipoVeiculoRequestDto("", 0);
        model.addAttribute("id", null);
        model.addAttribute("veiculo", veiculo);
        model.addAttribute("arquivado", false);
        return "veiculo/formVeiculo :: formVeiculo";
    }

    @HxRequest
    @GetMapping("/abrirFormulario/{id}")
    public String formEditar(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "false") boolean arquivado,
            Model model) {
        try {
            // BUG 5 CORRIGIDO: busca pelo DTO correto conforme o contexto
            TipoVeiculoRequestDto dto;
            if (arquivado) {
                TipoVeiculoArquivadoResponseDto v = application.buscarArquivado(id);
                dto = new TipoVeiculoRequestDto(v.nome(), v.capacidade());
            } else {
                TipoVeiculoResponseDto v = application.buscar(id);
                dto = new TipoVeiculoRequestDto(v.nome(), v.capacidade());
            }
            model.addAttribute("veiculo", dto);
            model.addAttribute("id", id);
            model.addAttribute("arquivado", arquivado);
            return "veiculo/formVeiculo :: formVeiculo";
        } catch (VeiculoNaoEncontradoException e) {
            model.addAttribute("erroGlobal", "Veículo não encontrado ou removido.");
            model.addAttribute("arquivado", arquivado);
            return "veiculo/formVeiculo :: formVeiculo";
        }
    }


    @HxRequest
    @PostMapping
    @HxTriggerAfterSettle("listar-ativos")
    public String salvar(
            Model model,
            @Valid @ModelAttribute("veiculo") TipoVeiculoRequestDto veiculo,
            BindingResult result) {

        if (result.hasErrors()) {

            return "veiculo/formVeiculo :: formVeiculo";
        }
        try {
            application.salvar(veiculo);
            return "layout/vazio :: vazio";
        } catch (VeiculoDuplicadoException e) {
            result.rejectValue("nome", "duplicado", "Já existe um veículo cadastrado com este nome.");
            return "veiculo/formVeiculo :: formVeiculo";
        }
    }

    @HxRequest
    @PutMapping("/{id}")
    @HxTriggerAfterSettle("listar-ativos")
    public String editar(
            @PathVariable UUID id,
            @Valid @ModelAttribute("veiculo") TipoVeiculoRequestDto veiculo,
            BindingResult result,
            @RequestParam(defaultValue = "false") boolean arquivado, // BUG 5 CORRIGIDO: recebe contexto de origem
            Model model) {

        if (result.hasErrors()) {
            return "veiculo/formVeiculo :: formVeiculo";
        }

        try {
            application.editar(id, veiculo);
            return detalhesVeiculo(id, arquivado, model); // BUG 5 CORRIGIDO: era hardcoded "false"; agora usa o contexto real
        } catch (VeiculoDuplicadoException e) {
            result.rejectValue("nome", "duplicado", "Já existe um veículo cadastrado com este nome.");
            return "veiculo/formVeiculo :: formVeiculo";
        } catch (VeiculoNaoEncontradoException e) {
            result.rejectValue("error", "IdNaoEncontrado", "Id não encontrado");
            return "veiculo/formVeiculo :: formVeiculo";
        }
    }


    @GetMapping("/{id}/detalhes")
    @HxRequest
    public String detalhesVeiculo(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "false") boolean arquivado,
            Model model) {

        Object veiculo = arquivado ? application.buscarArquivado(id) : application.buscar(id);
        List<TipoVeiculoHistoricoResponseDto> historico = application.buscarHistorico(id);

        model.addAttribute("arquivado", arquivado);
        model.addAttribute("veiculo", veiculo);
        model.addAttribute("historico", historico);

        return "veiculo/detalhesVeiculo :: detalhesVeiculo";
    }

    @HxRequest
    @DeleteMapping("/{id}")
    @HxTriggerAfterSettle({"listar-ativos","fechar-formulario"})
    @ResponseBody
    public void deletar(@PathVariable UUID id) {
        application.deletar(id);
    }
}
