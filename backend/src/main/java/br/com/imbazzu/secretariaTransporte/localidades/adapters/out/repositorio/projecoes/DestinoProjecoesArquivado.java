package br.com.imbazzu.secretariaTransporte.localidades.adapters.out.repositorio.projecoes;

import java.time.LocalDate;
import java.util.UUID;

public interface DestinoProjecoesArquivado {

    UUID getId();

    LocalDate getDataArquivado();

    String getNome();

    Integer getQtdViagens();
}
