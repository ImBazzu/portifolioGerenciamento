package br.com.imbazzu.secretariaTransporte.lista.adpter.out.repositorio.projecoes;

import java.time.LocalDate;
import java.util.UUID;

public interface TipoListaResumoProjecao {

    UUID getId();

    String getNome();

    String getDescricao();

    LocalDate getDataArquivado();

    long getTotalRegras();

    long getTotalCidades();
}
