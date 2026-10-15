package br.com.imbazzu.secretariaTransporte.localidades.adapters.out.repositorio.projecoes.cidade;

import java.time.Duration;
import java.util.UUID;

public interface CidadeProjecoes {
    UUID getId();
    String getNome();
    String getEstado();
    Duration getTempoViagem();
    Integer getTotalDestinos();
    
}
