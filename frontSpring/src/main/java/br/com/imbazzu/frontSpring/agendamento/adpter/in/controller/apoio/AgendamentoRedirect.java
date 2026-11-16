package br.com.imbazzu.frontSpring.agendamento.adpter.in.controller.apoio;

import java.time.LocalDate;

public final class AgendamentoRedirect {

    private AgendamentoRedirect() {
    }

    public static String dia(LocalDate data) {
        return "redirect:/agendamentos?data=" + data;
    }

    public static String carros(LocalDate data) {
        return "redirect:/agendamentos/carros?data=" + data;
    }

    public static String retorno(boolean paginaCarros, LocalDate data) {
        return paginaCarros ? carros(data) : dia(data);
    }
}
