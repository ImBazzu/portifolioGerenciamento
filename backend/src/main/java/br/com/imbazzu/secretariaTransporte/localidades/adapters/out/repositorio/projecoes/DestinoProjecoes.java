package br.com.imbazzu.secretariaTransporte.localidades.adapters.out.repositorio.projecoes;

import java.util.UUID;

public interface DestinoProjecoes {

    UUID getId();

    String getNome();

    Integer getQtdPassageiros();
}
