package br.com.imbazzu.secretariaTransporte.pessoa.application.query.buscar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.imbazzu.secretariaTransporte.pessoa.core.domain.Pessoa;
import br.com.imbazzu.secretariaTransporte.pessoa.core.excecoes.PessoaNaoEncontradoException;
import br.com.imbazzu.secretariaTransporte.pessoa.core.porta.out.PessoaPorta;

@ExtendWith(MockitoExtension.class)
class PessoaQueryBuscarHandlerTest {

    @Mock
    private PessoaPorta porta;

    @InjectMocks
    private PessoaQueryBuscarHandler handler;

    @Test
    void retornaPessoaEncontrada() {
        var id = UUID.randomUUID();
        var pessoa = org.mockito.Mockito.mock(Pessoa.class);
        when(porta.buscar(id)).thenReturn(Optional.of(pessoa));

        assertSame(pessoa, handler.handler(new PessoaQueryBuscar(id)));
        verify(porta).buscar(id);
    }

    @Test
    void lancaExcecaoQuandoPessoaNaoExiste() {
        var id = UUID.randomUUID();
        when(porta.buscar(id)).thenReturn(Optional.empty());

        var exception = assertThrows(
                PessoaNaoEncontradoException.class,
                () -> handler.handler(new PessoaQueryBuscar(id)));

        assertEquals("Id de Pessoa não encontrado.\nId: " + id, exception.getMessage());
    }
}
