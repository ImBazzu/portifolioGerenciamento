package br.com.imbazzu.secretariaTransporte.agendamento.adpter.out.repositorio.projecoes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Duration;
import java.util.UUID;

public interface HistoricoViagemProjecao {
    UUID getId();
    LocalDate getData();
    LocalTime getHorarioChegada();
    long getTempoViagemSegundos();
    int getTempoViagemNanos();
    default LocalTime getHorarioSaidaNecessario() {
        return getHorarioChegada().minus(Duration.ofSeconds(getTempoViagemSegundos(), getTempoViagemNanos()));
    }
    String getDestinoNome();
    String getListaDescricao();
    String getTipoVeiculoNome();
    boolean isBuscarNaResidencia();
    int getAcompanhantes();
}
