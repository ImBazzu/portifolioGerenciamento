package br.com.imbazzu.secretariaTransporte.tipoVeiculo.adpter.out.repositorio.projecoes;

import java.time.LocalDate;
import java.util.UUID;

public interface TipoVeiculoHistoricoProjecao {

    UUID getId();

    LocalDate getData();

    String getDestinos();

    Integer getQtdVeiculo();

    Integer getTotalPassageiros();
}
