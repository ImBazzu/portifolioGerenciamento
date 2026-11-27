package br.com.imbazzu.secretariaTransporte.veiculo.core.domain;

import java.time.LocalTime;

public final class OrdenacaoHorarioSaida {

    private static final int INICIO_PERIODO_NOTURNO = 20 * 60;
    private static final int MINUTOS_DIA = 24 * 60;

    private OrdenacaoHorarioSaida() {
    }

    public static int chave(LocalTime horario) {
        if (horario == null) {
            return Integer.MAX_VALUE;
        }

        int minutos = horario.getHour() * 60 + horario.getMinute();
        return minutos >= INICIO_PERIODO_NOTURNO ? minutos - MINUTOS_DIA : minutos;
    }
}
