package br.com.imbazzu.frontSpring.agendamento.adpter.in.controller.apoio;

import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Executa uma ação na API e devolve o redirect com flash de sucesso ou de erro. */
public class RespostaFlash {

    private final MensagemErroApi mensagemErro;

    public RespostaFlash(MensagemErroApi mensagemErro) {
        this.mensagemErro = mensagemErro;
    }

    public String executar(
            RedirectAttributes redirect, String mensagemSucesso, String destino, Runnable acao) {
        try {
            acao.run();
            redirect.addFlashAttribute("sucesso", mensagemSucesso);
        } catch (RestClientResponseException e) {
            redirect.addFlashAttribute("erro", mensagemErro.de(e));
        }
        return destino;
    }
}
