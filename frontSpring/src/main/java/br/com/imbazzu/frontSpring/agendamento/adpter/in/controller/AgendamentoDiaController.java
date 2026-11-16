package br.com.imbazzu.frontSpring.agendamento.adpter.in.controller;

import java.time.LocalDate;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.imbazzu.frontSpring.agendamento.adpter.in.controller.apoio.AgendamentoRedirect;
import br.com.imbazzu.frontSpring.agendamento.adpter.in.controller.apoio.MensagemErroApi;
import br.com.imbazzu.frontSpring.agendamento.adpter.in.controller.apoio.RespostaFlash;
import br.com.imbazzu.frontSpring.agendamento.adpter.out.api.AgendamentoApi;

/** Ações sobre o dia inteiro: abrir a agenda e rodar a separação automática. */
@Controller
@RequestMapping("/agendamentos")
public class AgendamentoDiaController {

    private final AgendamentoApi agendamentoApi;
    private final RespostaFlash respostaFlash;
    private final MensagemErroApi mensagemErro;

    public AgendamentoDiaController(
            AgendamentoApi agendamentoApi, RespostaFlash respostaFlash, MensagemErroApi mensagemErro) {
        this.agendamentoApi = agendamentoApi;
        this.respostaFlash = respostaFlash;
        this.mensagemErro = mensagemErro;
    }

    @PostMapping("/dias/abrir")
    public String abrirDia(@RequestParam LocalDate data, RedirectAttributes redirect) {
        return respostaFlash.executar(
                redirect,
                "Agenda de %s aberta.".formatted(data),
                AgendamentoRedirect.dia(data),
                () -> agendamentoApi.abrirDia(data));
    }

    @PostMapping("/dias/{data}/separacao-automatica")
    public String separarAutomaticamente(@PathVariable LocalDate data, RedirectAttributes redirect) {
        try {
            var resultado = agendamentoApi.separarAutomaticamente(data);
            redirect.addFlashAttribute("sucesso",
                    "%d passageiro(s) alocado(s). Pendentes: %d."
                            .formatted(resultado.passageirosAlocados(), resultado.passageirosPendentes().size()));
            if (!resultado.passageirosPendentes().isEmpty()) {
                redirect.addFlashAttribute("pendencias", resultado.passageirosPendentes());
            }
        } catch (RestClientResponseException e) {
            redirect.addFlashAttribute("erro", mensagemErro.de(e));
        }
        return AgendamentoRedirect.carros(data);
    }
}
