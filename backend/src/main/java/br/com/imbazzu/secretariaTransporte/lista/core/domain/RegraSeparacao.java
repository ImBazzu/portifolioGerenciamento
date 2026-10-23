package br.com.imbazzu.secretariaTransporte.lista.core.domain;

import java.time.Duration;
import java.util.Objects;
import java.util.UUID;

import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.DadosInvalidosException;

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
        validarTempoTolerancia(tempoTolerancia);
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
        Objects.requireNonNull(idTipoVeiculo,"Id do tipo do veiculo é obrigatório");
        Objects.requireNonNull(tempoTolerancia,"Tempo de tolerancia é obrigatório");
        validarTempoTolerancia(tempoTolerancia);
        this.idTipoVeiculo = idTipoVeiculo;
        this.tempoTolerancia = tempoTolerancia;
    }

    private static void validarTempoTolerancia(Duration tempoTolerancia) {
        if (tempoTolerancia.isNegative()) {
            throw new DadosInvalidosException("Tempo de tolerância não pode ser negativo");
        }
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
