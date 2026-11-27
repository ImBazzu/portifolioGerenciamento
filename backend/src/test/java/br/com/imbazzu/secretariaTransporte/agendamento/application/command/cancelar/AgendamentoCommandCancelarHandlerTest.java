package br.com.imbazzu.secretariaTransporte.agendamento.application.command.cancelar;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.imbazzu.secretariaTransporte.agendamento.core.porta.out.AgendamentoCommandPorta;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.EntidadeNaoEncontradoException;

@ExtendWith(MockitoExtension.class)
class AgendamentoCommandCancelarHandlerTest {

    @Mock
    private AgendamentoCommandPorta comandos;

    @InjectMocks
    private AgendamentoCommandCancelarHandler handler;

    @Test
    void deveCancelarERegistrarMotivoEAutor() {
        UUID id = UUID.randomUUID();
        UUID autorId = UUID.randomUUID();
        when(comandos.cancelar(eq(id), any(LocalDateTime.class), eq("Passageiro solicitou"), eq(autorId)))
                .thenReturn(true);

        handler.handler(new AgendamentoCommandCancelar(id, "  Passageiro solicitou  ", autorId));

        verify(comandos).cancelar(eq(id), any(LocalDateTime.class), eq("Passageiro solicitou"), eq(autorId));
    }

    @Test
    void devePermitirMotivoVazioComoNulo() {
        UUID id = UUID.randomUUID();
        when(comandos.cancelar(eq(id), any(LocalDateTime.class), isNull(), isNull())).thenReturn(true);

        handler.handler(new AgendamentoCommandCancelar(id, "  ", null));

        verify(comandos).cancelar(eq(id), any(LocalDateTime.class), isNull(), isNull());
    }

    @Test
    void deveInformarQuandoAgendamentoNaoEstaAtivo() {
        UUID id = UUID.randomUUID();
        when(comandos.cancelar(eq(id), any(LocalDateTime.class), isNull(), isNull())).thenReturn(false);

        assertThrows(EntidadeNaoEncontradoException.class,
                () -> handler.handler(new AgendamentoCommandCancelar(id, null, null)));
    }
}
