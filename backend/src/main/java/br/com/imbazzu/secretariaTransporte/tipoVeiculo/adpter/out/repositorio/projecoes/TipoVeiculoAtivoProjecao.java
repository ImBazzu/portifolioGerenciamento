package br.com.imbazzu.secretariaTransporte.tipoVeiculo.adpter.out.repositorio.projecoes;

import java.util.UUID;

public interface TipoVeiculoAtivoProjecao {

    UUID getId();

    String getNome();

    Integer getCapacidade();
}
