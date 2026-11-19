package br.com.imbazzu.frontSpring.especificacao.adpter.in.controller;

import java.util.UUID;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.imbazzu.frontSpring.especificacao.core.dto.EspecificacaoRequestDto;
import br.com.imbazzu.frontSpring.especificacao.core.excecoes.EspecificacaoDuplicadaException;
import br.com.imbazzu.frontSpring.especificacao.core.porta.in.EspecificacaoApplicationPorta;
import br.com.imbazzu.frontSpring.prioridade.PrioridadeApi;
import br.com.imbazzu.frontSpring.agendamento.adpter.in.controller.apoio.MensagemErroApi;
import org.springframework.web.client.RestClientResponseException;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxTriggerAfterSettle;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("especificacoes")
public class EspecificacaoController {
    private final EspecificacaoApplicationPorta application;
    private final ObjectMapper objectMapper;
    private final PrioridadeApi prioridades;
    private final MensagemErroApi mensagemErro;

    public EspecificacaoController(EspecificacaoApplicationPorta application, ObjectMapper objectMapper, PrioridadeApi prioridades, MensagemErroApi mensagemErro) {
        this.application = application;
        this.objectMapper = objectMapper;
        this.prioridades = prioridades;
        this.mensagemErro = mensagemErro;
    }

    @GetMapping
    public String inicio(Model model) {
        model.addAttribute("pagina", "especificacoes");
        return "paginas/especificacao/especificacao";
    }

    @HxRequest
    @GetMapping("/listar")
    public String listar(
            @RequestParam(defaultValue = "") String nome,
            @RequestParam(defaultValue = "false") boolean arquivadas,
            @RequestParam(defaultValue = "0") int pagina,
            Model model) {
        var resultado = application.listar(nome, arquivadas, pagina);
        model.addAttribute("especificacoes", resultado.conteudo());
        model.addAttribute("nome", nome);
        model.addAttribute("arquivadas", arquivadas);
        model.addAttribute("paginaAtual", resultado.paginaAtual());
        model.addAttribute("totalPaginas", resultado.totalPaginas());
        return arquivadas
                ? "paginas/especificacao/tabEspecificacoesArquivadas :: tabEspecificacoesArquivadas"
                : "paginas/especificacao/tabEspecificacoesAtivas :: tabEspecificacoesAtivas";
    }

    @HxRequest
    @GetMapping("/abrirFormulario")
    public String formularioNovo(Model model) {
        model.addAttribute("id", null);
        model.addAttribute("especificacao", new EspecificacaoRequestDto("", null, ""));
        model.addAttribute("prioridades", prioridades.listar());
        return "paginas/especificacao/formEspecificacao :: formEspecificacao";
    }

    @HxRequest
    @GetMapping("/abrirFormulario/{id}")
    public String formularioEdicao(@PathVariable UUID id, Model model) {
        var especificacao = application.buscar(id);
        model.addAttribute("id", id);
        model.addAttribute("prioridades", prioridades.listar());
        model.addAttribute("especificacao", new EspecificacaoRequestDto(
                especificacao.nome(), especificacao.prioridade(), especificacao.observacao()));
        return "paginas/especificacao/formEspecificacao :: formEspecificacao";
    }

    @HxRequest
    @GetMapping("/{id}/detalhes")
    public String detalhes(@PathVariable UUID id, Model model) {
        model.addAttribute("especificacao", application.buscar(id));
        return "paginas/especificacao/detalhesEspecificacao :: detalhesEspecificacao";
    }

    @HxRequest
    @GetMapping("/{id}/detalhes/arquivado")
    public String detalhesArquivado(@PathVariable UUID id, Model model) {
        model.addAttribute("arquivado", true);
        model.addAttribute("especificacao", application.buscarArquivado(id));
        return "paginas/especificacao/detalhesEspecificacao :: detalhesEspecificacao";
    }

    @HxRequest
    @PostMapping
    public String salvar(
            @Valid @ModelAttribute("especificacao") EspecificacaoRequestDto especificacao,
            BindingResult result,
            Model model,
            HttpServletResponse response) throws JsonProcessingException {
        
        model.addAttribute("id", null);
        model.addAttribute("prioridades", prioridades.listar());

        if (result.hasErrors()) {
            return "paginas/especificacao/formEspecificacao :: formEspecificacao";
        }
        
        try {
            UUID id = application.salvar(especificacao);
            var eventos = Map.of("atualizar-especificacoes", Map.of());
            response.setHeader("HX-Trigger-After-Settle", objectMapper.writeValueAsString(eventos));
            return "layout/vazio :: vazio";
        } catch (EspecificacaoDuplicadaException e) {
            result.rejectValue("nome", "duplicado", e.getMessage());
        } catch (Exception e) {
            model.addAttribute("erroGlobal", e.getMessage() != null ? e.getMessage() : "Ocorreu um erro inesperado.");
        }
        
        return "paginas/especificacao/formEspecificacao :: formEspecificacao";
    }

    @HxRequest
    @PutMapping("/{id}")
    @HxTriggerAfterSettle("atualizar-especificacoes")
    public String editar(
            @PathVariable UUID id,
            @Valid @ModelAttribute("especificacao") EspecificacaoRequestDto especificacao,
            BindingResult result,
            Model model) {
        
        model.addAttribute("id", id);
        model.addAttribute("prioridades", prioridades.listar());

        if (result.hasErrors()) {
            return "paginas/especificacao/formEspecificacao :: formEspecificacao";
        }
        
        try {
            application.editar(id, especificacao);
            return "layout/vazio :: vazio";
        } catch (EspecificacaoDuplicadaException e) {
            result.rejectValue("nome", "duplicado", e.getMessage());
        } catch (Exception e) {
            model.addAttribute("erroGlobal", e.getMessage() != null ? e.getMessage() : "Ocorreu um erro inesperado.");
        }
        
        return "paginas/especificacao/formEspecificacao :: formEspecificacao";
    }

    @HxRequest
    @PatchMapping("/{id}/arquivar")
    @HxTriggerAfterSettle("atualizar-especificacoes")
    public String arquivar(@PathVariable UUID id) {
        application.arquivar(id);
        return "layout/vazio :: vazio";
    }

    @HxRequest
    @PatchMapping("/{id}/desarquivar")
    @HxTriggerAfterSettle("atualizar-especificacoes")
    @ResponseBody
    public void desarquivar(@PathVariable UUID id) {
        application.desarquivar(id);
    }

    @HxRequest
    @DeleteMapping("/{id}")
    public String excluir(@PathVariable UUID id, Model model, HttpServletResponse response) {
        var especificacao = application.buscarArquivado(id);
        try {
            application.excluir(id);
            response.setHeader("HX-Trigger-After-Settle", "atualizar-especificacoes");
            return "layout/vazio :: vazio";
        } catch (RestClientResponseException e) {
            model.addAttribute("arquivado", true);
            model.addAttribute("especificacao", especificacao);
            model.addAttribute("erroGlobal", mensagemErro.de(e));
            return "paginas/especificacao/detalhesEspecificacao :: detalhesEspecificacao";
        }
    }

    @HxRequest
    @GetMapping("/limpar-modal")
    public String limparModal() {
        return "layout/vazio :: vazio";
    }
}
