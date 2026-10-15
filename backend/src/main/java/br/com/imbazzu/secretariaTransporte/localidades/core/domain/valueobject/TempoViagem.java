package br.com.imbazzu.secretariaTransporte.localidades.core.domain.valueobject;

import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.DadosInvalidosException;

import java.time.Duration;

public record TempoViagem(long hora, long minutos) {

    public TempoViagem {
        // Verifica se o valor da hora é valido
        if (hora < 0) {
            // Lança o erro
            throw new DadosInvalidosException("Hora invalida");
        }
        // Verifica se o minuto é valido
        if (minutos < 0 || minutos >= 60) {
            // Lança o erro
            throw new DadosInvalidosException("Minuto invalido");
        }
        if ((hora == 0) && (minutos == 0)) {
            throw new DadosInvalidosException("Tempo de viagem deve ser maior que zero");
        }
    }

    public Duration emDuration() {
        return Duration.ofHours(hora)
                .plusMinutes(minutos);
    }

    public String tempoFormatado() {
        return hora + ":" + minutos;
    }

    public TempoViagem(Duration tempoViagem) {
        this(tempoViagem.toHours(), tempoViagem.toMinutesPart());
    }
}