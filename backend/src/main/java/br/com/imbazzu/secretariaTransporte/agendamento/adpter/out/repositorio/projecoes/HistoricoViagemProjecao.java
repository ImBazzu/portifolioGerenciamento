package br.com.imbazzu.secretariaTransporte.agendamento.adpter.out.repositorio.projecoes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public interface HistoricoViagemProjecao {
    UUID getId();
    LocalDate getData();
    LocalTime getHorarioChegada();
    LocalTime getHorarioSaidaNecessario();
    String getDestinoNome();
    String getListaDescricao();
    String getTipoVeiculoNome();
    boolean isBuscarNaResidencia();
    int getAcompanhantes();
}
