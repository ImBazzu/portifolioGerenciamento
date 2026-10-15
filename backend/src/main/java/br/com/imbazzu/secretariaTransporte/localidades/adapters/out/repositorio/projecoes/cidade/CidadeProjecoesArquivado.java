package br.com.imbazzu.secretariaTransporte.localidades.adapters.out.repositorio.projecoes.cidade;

import java.time.Duration;
import java.time.LocalDate;
import java.util.UUID;

public interface CidadeProjecoesArquivado {
    UUID getId();
    String getNome();
    String getEstado();
    Duration getTempoViagem();
    Integer getTotalDestinos();
    LocalDate getDataArquivamento();
    
}
