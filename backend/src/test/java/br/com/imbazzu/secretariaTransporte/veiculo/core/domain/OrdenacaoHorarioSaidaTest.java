package br.com.imbazzu.secretariaTransporte.veiculo.core.domain;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalTime;

import org.junit.jupiter.api.Test;

class OrdenacaoHorarioSaidaTest {

    @Test
    void saidasNoturnasDevemFicarAntesDasSaidasDaMadrugada() {
        assertTrue(OrdenacaoHorarioSaida.chave(LocalTime.of(20, 0))
                < OrdenacaoHorarioSaida.chave(LocalTime.of(5, 30)));
        assertTrue(OrdenacaoHorarioSaida.chave(LocalTime.of(23, 59))
                < OrdenacaoHorarioSaida.chave(LocalTime.of(0, 1)));
    }

    @Test
    void horariosNaoInformadosDevemFicarNoFim() {
        assertTrue(OrdenacaoHorarioSaida.chave(null)
                > OrdenacaoHorarioSaida.chave(LocalTime.of(23, 0)));
    }
}
