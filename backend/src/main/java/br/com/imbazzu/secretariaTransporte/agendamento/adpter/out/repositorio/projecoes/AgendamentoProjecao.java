package br.com.imbazzu.secretariaTransporte.agendamento.adpter.out.repositorio.projecoes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public interface AgendamentoProjecao {

    UUID getId();

    LocalDate getData();

    UUID getIdPessoa();

    UUID getIdLista();

    UUID getIdRegraCadastro();

    UUID getIdTipoVeiculo();

    UUID getIdDestino();

    long getTempoViagemSegundos();

    int getTempoViagemNanos();

    long getToleranciaSegundos();

    int getToleranciaNanos();

    LocalTime getHorarioChegada();

    boolean isBuscarNaResidencia();

    int getAcompanhantes();

    UUID getIdVeiculoAberto();
}
