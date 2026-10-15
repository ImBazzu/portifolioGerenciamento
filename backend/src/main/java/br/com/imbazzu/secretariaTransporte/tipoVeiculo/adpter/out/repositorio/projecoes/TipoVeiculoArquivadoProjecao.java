package br.com.imbazzu.secretariaTransporte.tipoVeiculo.adpter.out.repositorio.projecoes;

import java.time.LocalDate;
import java.util.UUID;

public interface TipoVeiculoArquivadoProjecao {

    UUID getId();

    String getNome();

    Integer getCapacidade();

    LocalDate getDataArquivado();
}
