package br.com.imbazzu.frontSpring.prioridade;

import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.imbazzu.frontSpring.agendamento.adpter.in.controller.apoio.MensagemErroApi;

@Controller
@RequestMapping("/prioridades")
public class PrioridadeController {
    private final PrioridadeApi api;
    private final MensagemErroApi mensagemErro;

    public PrioridadeController(PrioridadeApi api, MensagemErroApi mensagemErro) {
        this.api = api;
        this.mensagemErro = mensagemErro;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("pagina", "prioridades");
        model.addAttribute("prioridades", api.listar());
        return "paginas/prioridade/prioridades";
    }

    @PostMapping
    public String criar(@RequestParam String nome,
                        @RequestParam(defaultValue = "false") boolean buscarNaResidencia,
                        @RequestParam int quantidadeAcompanhantes, RedirectAttributes redirect) {
        try {
            api.criar(new PrioridadeApi.Dados(nome, buscarNaResidencia, quantidadeAcompanhantes));
            redirect.addFlashAttribute("sucesso", "Prioridade cadastrada.");
        } catch (RestClientResponseException e) {
            redirect.addFlashAttribute("erro", mensagemErro.de(e));
        }
        return "redirect:/prioridades";
    }

    @PostMapping("/{id}")
    public String editar(@PathVariable UUID id, @RequestParam String nome,
                         @RequestParam(defaultValue = "false") boolean buscarNaResidencia,
                         @RequestParam int quantidadeAcompanhantes, RedirectAttributes redirect) {
        try {
            api.editar(id, new PrioridadeApi.Dados(nome, buscarNaResidencia, quantidadeAcompanhantes));
            redirect.addFlashAttribute("sucesso", "Prioridade atualizada.");
        } catch (RestClientResponseException e) {
            redirect.addFlashAttribute("erro", mensagemErro.de(e));
        }
        return "redirect:/prioridades";
    }

    @PostMapping("/{id}/arquivar")
    public String arquivar(@PathVariable UUID id, RedirectAttributes redirect) {
        return alterarStatus(id, true, redirect);
    }

    @PostMapping("/{id}/desarquivar")
    public String desarquivar(@PathVariable UUID id, RedirectAttributes redirect) {
        return alterarStatus(id, false, redirect);
    }

    private String alterarStatus(UUID id, boolean arquivar, RedirectAttributes redirect) {
        try {
            api.alterarStatus(id, arquivar);
            redirect.addFlashAttribute("sucesso", arquivar ? "Prioridade arquivada." : "Prioridade reativada.");
        } catch (RestClientResponseException e) {
            redirect.addFlashAttribute("erro", mensagemErro.de(e));
        }
        return "redirect:/prioridades";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable UUID id, RedirectAttributes redirect) {
        try {
            api.excluir(id);
            redirect.addFlashAttribute("sucesso", "Prioridade excluída.");
        } catch (RestClientResponseException e) {
            redirect.addFlashAttribute("erro", mensagemErro.de(e));
        }
        return "redirect:/prioridades";
    }
}
