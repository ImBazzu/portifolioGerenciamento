package br.com.imbazzu.secretariaTransporte.veiculo.application.command.abrirVeiculo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.imbazzu.secretariaTransporte.agendamento.core.model.TipoVeiculoAgendaDados;
import br.com.imbazzu.secretariaTransporte.agendamento.core.porta.out.AgendamentoCommandPorta;
import br.com.imbazzu.secretariaTransporte.agendamento.core.porta.out.AgendamentoQueryPorta;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.DadosInvalidosException;
import br.com.imbazzu.secretariaTransporte.veiculo.core.domain.FinalidadeAberturaVeiculo;
import br.com.imbazzu.secretariaTransporte.veiculo.core.domain.VeiculoAberto;

@ExtendWith(MockitoExtension.class)
class AgendamentoCommandAbrirVeiculoHandlerTest {

    @Mock
    private AgendamentoCommandPorta comandos;

    @Mock
    private AgendamentoQueryPorta consultas;

    @InjectMocks
    private AgendamentoCommandAbrirVeiculoHandler handler;

    @Test
    void abreCarroParaEmergenciaComListaEHorarioDeSaidaManual() {
        var tipoId = UUID.randomUUID();
        var listaId = UUID.randomUUID();
        var data = LocalDate.of(2026, 11, 27);
        var saida = LocalTime.of(22, 0);
        when(consultas.diaAberto(data)).thenReturn(true);
        when(consultas.buscarTipoVeiculo(tipoId)).thenReturn(new TipoVeiculoAgendaDados(tipoId, "Carro", 4, true));
        when(consultas.listaAtiva(listaId)).thenReturn(true);
        when(comandos.salvar(any(VeiculoAberto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var veiculo = handler.handler(new AgendamentoCommandAbrirVeiculo(
                data, tipoId, "Carro 01", saida, FinalidadeAberturaVeiculo.EMERGENCIA, listaId));

        assertEquals(saida, veiculo.horarioSaida());
        assertFalse(veiculo.horarioSaidaCalculado());
        assertEquals(FinalidadeAberturaVeiculo.EMERGENCIA, veiculo.finalidadeAbertura());
        assertEquals(listaId, veiculo.idListaAbertura());
    }

    @Test
    void carroParaAjusteMantemSaidaCalculada() {
        var tipoId = UUID.randomUUID();
        var data = LocalDate.of(2026, 11, 27);
        when(consultas.diaAberto(data)).thenReturn(true);
        when(consultas.buscarTipoVeiculo(tipoId)).thenReturn(new TipoVeiculoAgendaDados(tipoId, "Carro", 4, true));
        when(comandos.salvar(any(VeiculoAberto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var veiculo = handler.handler(new AgendamentoCommandAbrirVeiculo(
                data, tipoId, null, null, FinalidadeAberturaVeiculo.AJUSTE, null));

        assertTrue(veiculo.horarioSaidaCalculado());
        assertEquals(FinalidadeAberturaVeiculo.AJUSTE, veiculo.finalidadeAbertura());
    }

    @Test
    void exigeListaEHorarioParaAlta() {
        var tipoId = UUID.randomUUID();
        var data = LocalDate.of(2026, 11, 27);
        when(consultas.diaAberto(data)).thenReturn(true);
        when(consultas.buscarTipoVeiculo(tipoId)).thenReturn(new TipoVeiculoAgendaDados(tipoId, "Carro", 4, true));

        assertThrows(DadosInvalidosException.class, () -> handler.handler(new AgendamentoCommandAbrirVeiculo(
                data, tipoId, null, null, FinalidadeAberturaVeiculo.ALTA, null)));
    }

    @Test
    void rejeitaCarroEspecialComCapacidadeDiferenteDeQuatro() {
        var tipoId = UUID.randomUUID();
        var data = LocalDate.of(2026, 11, 27);
        when(consultas.diaAberto(data)).thenReturn(true);
        when(consultas.buscarTipoVeiculo(tipoId)).thenReturn(new TipoVeiculoAgendaDados(tipoId, "Carro", 5, true));

        assertThrows(DadosInvalidosException.class, () -> handler.handler(new AgendamentoCommandAbrirVeiculo(
                data, tipoId, null, LocalTime.NOON, FinalidadeAberturaVeiculo.ALTA, UUID.randomUUID())));
    }
}
