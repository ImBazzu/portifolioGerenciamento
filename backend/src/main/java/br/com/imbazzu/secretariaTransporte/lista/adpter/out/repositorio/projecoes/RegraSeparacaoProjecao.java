package br.com.imbazzu.secretariaTransporte.lista.adpter.out.repositorio.projecoes;

import java.util.UUID;

public interface RegraSeparacaoProjecao {

    UUID getId();

    TipoVeiculoProjecao getTipoVeiculo();

    long getTempoToleranciaSegundos();

    int getTempoToleranciaNanos();

    interface TipoVeiculoProjecao {
        String getNome();
    }
}