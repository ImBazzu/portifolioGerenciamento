package br.com.imbazzu.secretariaTransporte.agendamento.adpter.out.repositorio.projecoes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

public interface AgendamentoCanceladoProjecao {
    UUID getId();
    LocalDate getData();
    UUID getIdPessoa();
    UUID getIdLista();
    UUID getIdTipoVeiculo();
    UUID getIdDestino();
    LocalTime getHorarioChegada();
    boolean isBuscarNaResidencia();
    int getAcompanhantes();
    String getAutorNome();
    LocalDateTime getCanceladoEm();
    String getMotivoCancelamento();
}
