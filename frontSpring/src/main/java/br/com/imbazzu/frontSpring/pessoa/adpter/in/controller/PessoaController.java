package br.com.imbazzu.frontSpring.pessoa.adpter.in.controller;

import java.util.UUID;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.imbazzu.frontSpring.especificacao.core.porta.in.EspecificacaoApplicationPorta;
import br.com.imbazzu.frontSpring.especificacao.core.dto.EspecificacaoResponseDto;
import br.com.imbazzu.frontSpring.controller.dto.ResultadoPaginado;
import br.com.imbazzu.frontSpring.pessoa.core.dto.PessoaRequestDto;
import br.com.imbazzu.frontSpring.pessoa.core.excecoes.PessoaDuplicadaException;
import br.com.imbazzu.frontSpring.pessoa.core.porta.in.PessoaApplicationPorta;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxTriggerAfterSettle;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("pessoas")
public class PessoaController {
    private final PessoaApplicationPorta application;
    private final EspecificacaoApplicationPorta especificacaoApplication;
    private final ObjectMapper objectMapper;

    public PessoaController(PessoaApplicationPorta application, EspecificacaoApplicationPorta especificacaoApplication, ObjectMapper objectMapper) {
        this.application = application;
        this.especificacaoApplication = especificacaoApplication;
        this.objectMapper = objectMapper;
    }

    @GetMapping
    public String inicio(Model model) {
        model.addAttribute("pagina", "pessoas");
        return "paginas/pessoa/pessoa";
    }

    @HxRequest
    @GetMapping("/listar")
    public String listar(
            @RequestParam(defaultValue = "") String nome,
            @RequestParam(defaultValue = "false") boolean arquivadas,
            @RequestParam(required = false) UUID especificacaoId,
            @RequestParam(defaultValue = "0") int pagina,
            Model model) {
        var resultado = especificacaoId == null
                ? application.listar(nome, arquivadas, pagina)
                : application.listarPorEspecificacao(nome, especificacaoId, arquivadas, pagina);
        model.addAttribute("pessoas", resultado.conteudo());
        model.addAttribute("especificacoesFiltro", especificacoesParaFiltro());
        model.addAttribute("especificacaoId", especificacaoId);
        model.addAttribute("nome", nome);
        model.addAttribute("arquivadas", arquivadas);
        model.addAttribute("paginaAtual", resultado.paginaAtual());
        model.addAttribute("totalPaginas", resultado.totalPaginas());
        return arquivadas
                ? "paginas/pessoa/tabPessoasArquivadas :: tabPessoasArquivadas"
                : "paginas/pessoa/tabPessoasAtivas :: tabPessoasAtivas";
    }

    private List<EspecificacaoResponseDto> especificacoesParaFiltro() {
        var todas = new ArrayList<EspecificacaoResponseDto>();
        for (boolean arquivada : new boolean[] {false, true}) {
            int pagina = 0;
            ResultadoPaginado<EspecificacaoResponseDto> resultado;
            do {
                resultado = especificacaoApplication.listar("", arquivada, pagina++);
                todas.addAll(resultado.conteudo());
            } while (pagina < resultado.totalPaginas());
        }
        todas.sort(Comparator.comparing(EspecificacaoResponseDto::nome, String.CASE_INSENSITIVE_ORDER));
        return todas;
    }

    @HxRequest
    @GetMapping("/abrirFormulario")
    public String formularioNovo(
            @RequestParam(defaultValue = "false") boolean contextoAgendamento,
            Model model) {
        model.addAttribute("id", null);
        model.addAttribute("pessoa", new PessoaRequestDto("", "", null, "", "", "", false));
        model.addAttribute("contextoAgendamento", contextoAgendamento);
        model.addAttribute("especificacaoSelecionadaLabel", "");
        return "paginas/pessoa/formPessoa :: formPessoa";
    }

    @HxRequest
    @GetMapping("/abrirFormulario/{id}")
    public String formularioEdicao(@PathVariable UUID id, Model model, @RequestParam(defaultValue  = "false") boolean contextoAgendamento) {
        var pessoa = application.buscar(id);
        model.addAttribute("id", id);
        model.addAttribute("contextoAgendamento", contextoAgendamento);
        model.addAttribute("especificacaoSelecionadaLabel",
                pessoa.especificacaoNome() + " (" + pessoa.especificacaoPrioridade() + ")");
        model.addAttribute("pessoa", new PessoaRequestDto(
                pessoa.nome(), pessoa.observacao(), pessoa.especificacaoId(),
                pessoa.endereco(), pessoa.cpf(), pessoa.telefone(), pessoa.buscarNaResidencia()));
        return "paginas/pessoa/formPessoa :: formPessoa";
    }

    @HxRequest
    @GetMapping("/{id}/detalhes")
    public String detalhes(@PathVariable UUID id, Model model) {
        model.addAttribute("pessoa", application.buscar(id));
        return "paginas/pessoa/detalhesPessoa :: detalhesPessoa";
    }

    @HxRequest
    @GetMapping("/{id}/detalhes/arquivado")
    public String detalhesArquivado(@PathVariable UUID id,
                                    @RequestParam(defaultValue = "") String nome,
                                    @RequestParam(required = false) UUID especificacaoId,
                                    @RequestParam(defaultValue = "0") int pagina, Model model) {
        model.addAttribute("arquivado", true);
        model.addAttribute("nome", nome);
        model.addAttribute("especificacaoId", especificacaoId);
        model.addAttribute("paginaAtual", pagina);
        model.addAttribute("pessoa", application.buscarArquivado(id));
        return "paginas/pessoa/detalhesPessoa :: detalhesPessoa";
    }

    @HxRequest
    @PostMapping
    public String salvar(
            @Valid @ModelAttribute("pessoa") PessoaRequestDto pessoa,
            BindingResult result,
            @RequestParam(defaultValue = "false") boolean contextoAgendamento,
            @RequestParam(defaultValue = "") String termoEspecificacao,
            Model model,
            HttpServletResponse response) throws JsonProcessingException {
        
        model.addAttribute("id", null);
        model.addAttribute("contextoAgendamento", contextoAgendamento);
        model.addAttribute("especificacaoSelecionadaLabel", termoEspecificacao);

        if (result.hasErrors()) {
            return "paginas/pessoa/formPessoa :: formPessoa";
        }
        
        try {
            UUID id = application.salvar(pessoa);
            var eventos = contextoAgendamento
                    ? Map.of(
                            "atualizar-pessoas", Map.of(),
                            "pessoa-criada", Map.of(
                                    "id", id,
                                    "nome", pessoa.nome(),
                                    "especificacaoId", pessoa.especificacaoId()))
                    : Map.of("atualizar-pessoas", Map.of());
            response.setHeader("HX-Trigger-After-Settle", objectMapper.writeValueAsString(eventos));
            return "layout/vazio :: vazio";
        } catch (PessoaDuplicadaException e) {
            result.rejectValue("cpf", "duplicado", e.getMessage());
        } catch (Exception e) {
            model.addAttribute("erroGlobal", e.getMessage() != null ? e.getMessage() : "Ocorreu um erro inesperado.");
        }
        
        return "paginas/pessoa/formPessoa :: formPessoa";
    }

    @HxRequest
    @PutMapping("/{id}")
    public String editar(
            @PathVariable UUID id,
            @Valid @ModelAttribute("pessoa") PessoaRequestDto pessoa,
            BindingResult result,
            @RequestParam(defaultValue = "false") boolean contextoAgendamento,
            @RequestParam(defaultValue = "") String termoEspecificacao,
            Model model,
            HttpServletResponse response) {
        
        model.addAttribute("id", id);
        model.addAttribute("contextoAgendamento", contextoAgendamento);
        model.addAttribute("especificacaoSelecionadaLabel", termoEspecificacao);

        if (result.hasErrors()) {
            return "paginas/pessoa/formPessoa :: formPessoa";
        }
        
        try {
            application.editar(id, pessoa);
            response.setHeader(contextoAgendamento ? "HX-Refresh" : "HX-Trigger-After-Settle",
                    contextoAgendamento ? "true" : "atualizar-pessoas");
            return "layout/vazio :: vazio";
        } catch (PessoaDuplicadaException e) {
            result.rejectValue("cpf", "duplicado", e.getMessage());
        } catch (Exception e) {
            model.addAttribute("erroGlobal", e.getMessage() != null ? e.getMessage() : "Ocorreu um erro inesperado.");
        }
        
        return "paginas/pessoa/formPessoa :: formPessoa";
    }
    @HxRequest
    @PatchMapping("/{id}/arquivar")
    @HxTriggerAfterSettle("atualizar-pessoas")
    public String arquivar(@PathVariable UUID id) {
        application.arquivar(id);
        return "layout/vazio :: vazio";
    }

    @HxRequest
    @PatchMapping("/{id}/desarquivar")
    @HxTriggerAfterSettle("atualizar-pessoas")
    @ResponseBody
    public void desarquivar(@PathVariable UUID id) {
        application.desarquivar(id);
    }

    @HxRequest
    @PatchMapping("/{id}/validar")
    @HxTriggerAfterSettle("atualizar-pessoas")
    public String validar(@PathVariable UUID id, Model model) {
        application.validar(id);
        model.addAttribute("pessoa", application.buscar(id));
        return "paginas/pessoa/detalhesPessoa :: detalhesPessoa";
    }

    @HxRequest
    @GetMapping("/limpar-modal")
    public String limparModal() {
        return "layout/vazio :: vazio";
    }

    @HxRequest
    @GetMapping("/buscar-especificacoes")
    public String buscarEspecificacoes(
            @RequestParam(name = "termoEspecificacao", defaultValue = "") String termo,
            Model model) {
        var termoLimpo = termo.strip();
        var especificacoes = termoLimpo.length() < 2
                ? List.<EspecificacaoResponseDto>of()
                : especificacaoApplication.listar(termoLimpo, false, 0).conteudo();
        model.addAttribute("especificacoes", especificacoes);
        return "paginas/pessoa/resultados-especificacoes :: resultados";
    }

}
