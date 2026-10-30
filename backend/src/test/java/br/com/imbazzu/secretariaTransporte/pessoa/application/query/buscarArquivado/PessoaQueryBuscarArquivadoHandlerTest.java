package br.com.imbazzu.secretariaTransporte.pessoa.application.query.buscarArquivado;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
class PessoaQueryBuscarArquivadoHandlerTest {

    @Mock
    private PessoaPorta porta;

    @InjectMocks
    private PessoaQueryBuscarArquivadoHandler handler;

    @Test
    void retornaPessoaArquivadaEncontrada() {
        var id = UUID.randomUUID();
        var pessoa = org.mockito.Mockito.mock(Pessoa.class);
        when(porta.buscarArquivado(id)).thenReturn(Optional.of(pessoa));

        assertSame(pessoa, handler.handler(new PessoaQueryBuscarArquivado(id)));
    }

    @Test
    void lancaExcecaoQuandoPessoaArquivadaNaoExiste() {
        var id = UUID.randomUUID();
        when(porta.buscarArquivado(id)).thenReturn(Optional.empty());

        assertThrows(
                PessoaNaoEncontradoException.class,
                () -> handler.handler(new PessoaQueryBuscarArquivado(id)));
    }
}
