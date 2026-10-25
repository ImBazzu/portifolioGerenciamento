package br.com.imbazzu.secretariaTransporte.agendamento.application.model;

import java.util.List;
import java.util.UUID;

public record ResultadoSeparacaoAutomatica(
        int passageirosAlocados,
        List<PassageiroPendente> passageirosPendentes) {

    public record PassageiroPendente(UUID idAgendamento, String motivo) {
    }
}
