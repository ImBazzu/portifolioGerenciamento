package br.com.imbazzu.secretariaTransporte.agendamento.application.command.remover;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.imbazzu.secretariaTransporte.agendamento.core.model.AgendamentoRemocaoDados;
import br.com.imbazzu.secretariaTransporte.agendamento.core.porta.out.AgendamentoCommandPorta;
import br.com.imbazzu.secretariaTransporte.agendamento.core.porta.out.AgendamentoQueryPorta;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.OperacaoNaoPermitidaException;

@ExtendWith(MockitoExtension.class)
class AgendamentoCommandRemoverHandlerTest {

    @Mock
    private AgendamentoCommandPorta comandos;

    @Mock
    private AgendamentoQueryPorta consultas;

    @InjectMocks
    private AgendamentoCommandRemoverHandler handler;

    @Test
    void permiteExcluirApenasAgendamentoCanceladoSemVeiculo() {
        UUID id = UUID.randomUUID();
        when(consultas.buscarDadosRemocao(id))
                .thenReturn(Optional.of(new AgendamentoRemocaoDados(null, true)));

        handler.handler(new AgendamentoCommandRemover(id));

        verify(comandos).deletar(id);
    }

    @Test
    void impedeExcluirViagemAntesDeCancelar() {
        UUID id = UUID.randomUUID();
        when(consultas.buscarDadosRemocao(id))
                .thenReturn(Optional.of(new AgendamentoRemocaoDados(null, false)));

        assertThrows(OperacaoNaoPermitidaException.class,
                () -> handler.handler(new AgendamentoCommandRemover(id)));

        verify(comandos, never()).deletar(id);
    }
}
