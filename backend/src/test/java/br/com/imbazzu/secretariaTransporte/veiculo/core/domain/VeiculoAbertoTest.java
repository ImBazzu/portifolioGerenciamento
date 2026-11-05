package br.com.imbazzu.secretariaTransporte.veiculo.core.domain;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class VeiculoAbertoTest {

    @Test
    void calculaHorarioDeSaidaSomenteParaTipoCarro() {
        assertTrue(VeiculoAberto.tipoCalculaHorarioSaida("Carro"));
        assertFalse(VeiculoAberto.tipoCalculaHorarioSaida("Van"));
    }
}
