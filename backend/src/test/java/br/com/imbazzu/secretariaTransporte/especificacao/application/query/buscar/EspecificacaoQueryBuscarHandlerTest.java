package br.com.imbazzu.secretariaTransporte.especificacao.application.query.buscar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import br.com.imbazzu.secretariaTransporte.especificacao.adpter.out.jpaAdapter.EspecificacaoQueryAdapter;
import br.com.imbazzu.secretariaTransporte.especificacao.core.domain.Especificacao;
import br.com.imbazzu.secretariaTransporte.especificacao.core.excecoes.EspecificacaoNaoEncontradoException;

class EspecificacaoQueryBuscarHandlerTest {
    @Test
    void buscaSomenteEspecificacaoArquivada() {
        var adapter = mock(EspecificacaoQueryAdapter.class);
        var handler = new EspecificacaoQueryBuscarHandler(adapter);
        var id = UUID.randomUUID();
        var arquivada = Especificacao.recriar(id, "Transporte", UUID.randomUUID(), "", LocalDate.now());
        when(adapter.buscarArquivado(id)).thenReturn(Optional.of(arquivada));

        assertEquals(arquivada, handler.buscarArquivada(new EspecificacaoQueryBuscar(id)));
        assertThrows(EspecificacaoNaoEncontradoException.class,
                () -> handler.buscarArquivada(new EspecificacaoQueryBuscar(UUID.randomUUID())));
    }
}
