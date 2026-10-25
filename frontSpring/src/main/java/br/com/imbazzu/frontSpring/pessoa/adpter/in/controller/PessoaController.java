package br.com.imbazzu.frontSpring.pessoa.adpter.in.controller;

import java.util.UUID;

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

import br.com.imbazzu.frontSpring.pessoa.core.dto.PessoaPrioridade;
import br.com.imbazzu.frontSpring.pessoa.core.dto.PessoaRequestDto;
import br.com.imbazzu.frontSpring.pessoa.core.excecoes.PessoaDadosInvalidosException;
import br.com.imbazzu.frontSpring.pessoa.core.excecoes.PessoaDuplicadaException;
import br.com.imbazzu.frontSpring.pessoa.core.excecoes.PessoaNaoEncontradaException;
import br.com.imbazzu.frontSpring.pessoa.core.porta.in.PessoaApplicationPorta;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxTriggerAfterSettle;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("pessoas")
public class PessoaController {
    private final PessoaApplicationPorta application;

    public PessoaController(PessoaApplicationPorta application) {
        this.application = application;
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
            @RequestParam(defaultValue = "0") int pagina,
            Model model) {
        var resultado = application.listar(nome, arquivadas, pagina);
        model.addAttribute("pessoas", resultado.conteudo());
        model.addAttribute("nome", nome);
        model.addAttribute("arquivadas", arquivadas);
        model.addAttribute("paginaAtual", resultado.paginaAtual());
        model.addAttribute("totalPaginas", resultado.totalPaginas());
        return arquivadas
                ? "paginas/pessoa/tabPessoasArquivadas :: tabPessoasArquivadas"
                : "paginas/pessoa/tabPessoasAtivas :: tabPessoasAtivas";
    }

    @HxRequest
    @GetMapping("/abrirFormulario")
    public String formularioNovo(Model model) {
        model.addAttribute("id", null);
        model.addAttribute("pessoa", new PessoaRequestDto("", "", PessoaPrioridade.MODERADA, "", "", ""));
        return "paginas/pessoa/formPessoa :: formPessoa";
    }

    @HxRequest
    @GetMapping("/abrirFormulario/{id}")
    public String formularioEdicao(@PathVariable UUID id, Model model) {
        var pessoa = application.buscar(id);
        model.addAttribute("id", id);
        model.addAttribute("pessoa", new PessoaRequestDto(
                pessoa.nome(), pessoa.observacao(), pessoa.prioridade(), pessoa.endereco(), pessoa.cpf(),
                pessoa.telefone()));
        return "paginas/pessoa/formPessoa :: formPessoa";
    }

    @HxRequest
    @PostMapping
    @HxTriggerAfterSettle("atualizar-pessoas")
    public String salvar(
            @Valid @ModelAttribute("pessoa") PessoaRequestDto pessoa,
            BindingResult result,
            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("id", null);
            return "paginas/pessoa/formPessoa :: formPessoa";
        }
        try {
            application.salvar(pessoa);
            return "layout/vazio :: vazio";
        } catch (PessoaDuplicadaException e) {
            result.rejectValue("cpf", "duplicado", e.getMessage());
        } catch (PessoaDadosInvalidosException e) {
            model.addAttribute("erroGlobal", e.getMessage());
        }
        model.addAttribute("id", null);
        return "paginas/pessoa/formPessoa :: formPessoa";
    }

    @HxRequest
    @PutMapping("/{id}")
    @HxTriggerAfterSettle("atualizar-pessoas")
    public String editar(
            @PathVariable UUID id,
            @Valid @ModelAttribute("pessoa") PessoaRequestDto pessoa,
            BindingResult result,
            Model model) {
        model.addAttribute("id", id);
        if (result.hasErrors()) {
            return "paginas/pessoa/formPessoa :: formPessoa";
        }
        try {
            application.editar(id, pessoa);
            return "layout/vazio :: vazio";
        } catch (PessoaDuplicadaException e) {
            result.rejectValue("cpf", "duplicado", e.getMessage());
        } catch (PessoaDadosInvalidosException e) {
            model.addAttribute("erroGlobal", e.getMessage());
        } catch (PessoaNaoEncontradaException e) {
            model.addAttribute("erroGlobal", e.getMessage());
        }
        return "paginas/pessoa/formPessoa :: formPessoa";
    }

    @HxRequest
    @PatchMapping("/{id}/arquivar")
    @HxTriggerAfterSettle("atualizar-pessoas")
    @ResponseBody
    public void arquivar(@PathVariable UUID id) {
        application.arquivar(id);
    }

    @HxRequest
    @PatchMapping("/{id}/desarquivar")
    @HxTriggerAfterSettle("atualizar-pessoas")
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
