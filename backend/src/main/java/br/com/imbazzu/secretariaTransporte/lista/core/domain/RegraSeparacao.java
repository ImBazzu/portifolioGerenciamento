package br.com.imbazzu.secretariaTransporte.lista.core.domain;

import java.time.Duration;
import java.util.Objects;
import java.util.UUID;

public class RegraSeparacao {
 
    private final UUID id;

    private final UUID idLista;

    private  UUID idTipoVeiculo;

    private  Duration tempoTolerancia;


    private RegraSeparacao(UUID id,UUID idLista, UUID idTipoVeiculo, Duration tempoTolerancia){
        Objects.requireNonNull(id, "Id é obrigatório" );
        Objects.requireNonNull(idLista,"Id da lista é obrigatório");
        Objects.requireNonNull(idTipoVeiculo,"Id do tipo do veiculo é obrigatório");
        Objects.requireNonNull(tempoTolerancia,"Tempo de tolerancia é obrigatório");
        this.id = id;
        this.idLista = idLista;
        this.idTipoVeiculo = idTipoVeiculo;
        this.tempoTolerancia = tempoTolerancia;
    }


    public static RegraSeparacao criar(UUID idLista, UUID idTipoVeiculo, Duration tempoTolerancia){
        return new RegraSeparacao(UUID.randomUUID(), idLista, idTipoVeiculo, tempoTolerancia);
    }

    public static RegraSeparacao recriar(UUID id, UUID idLista, UUID idTipoVeiculo, Duration tempoTolerancia){
        return new RegraSeparacao(id, idLista, idTipoVeiculo, tempoTolerancia);
    }


    public void editar(UUID idTipoVeiculo, Duration tempoTolerancia){
        editarTipoVeiculo(idTipoVeiculo);
        editarTempoTolerancia(tempoTolerancia);
    }

    private void editarTempoTolerancia(Duration tempoTolerancia){
        Objects.requireNonNull(tempoTolerancia,"Tempo de tolerancia é obrigatório");
        this.tempoTolerancia = tempoTolerancia;
    }

    private void editarTipoVeiculo(UUID idTipoVeiculo){
        Objects.requireNonNull(idTipoVeiculo,"Id do tipo do veiculo é obrigatório");
        this.idTipoVeiculo = idTipoVeiculo;
    }

    public UUID getId() {
        return id;
    }

    public UUID getIdLista() {
        return idLista;
    }

    public UUID getIdTipoVeiculo() {
        return idTipoVeiculo;
    }

    public Duration getTempoTolerancia() {
        return tempoTolerancia;
    }
    
}
