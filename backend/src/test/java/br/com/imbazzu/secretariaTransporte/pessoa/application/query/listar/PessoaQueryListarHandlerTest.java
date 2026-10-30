package br.com.imbazzu.secretariaTransporte.pessoa.application.query.listar;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.imbazzu.secretariaTransporte.compartilhados.domain.pagination.ResultadoPaginado;
import br.com.imbazzu.secretariaTransporte.pessoa.application.PessoaApplication;
import br.com.imbazzu.secretariaTransporte.pessoa.core.domain.Pessoa;

@ExtendWith(MockitoExtension.class)
class PessoaQueryListarHandlerTest {

    @Mock
    private PessoaApplication application;

    @InjectMocks
    private PessoaQueryListarHandler handler;

    @Test
    void listaPessoasAtivasComOsFiltrosRecebidos() {
        var query = new PessoaQueryListar(" Maria ", 2, 10);
        var resultado = new ResultadoPaginado<Pessoa>(List.of(), 2, 10, 0, 0);
        when(application.listar(query.nome(), false, query.pagina(), query.tamanho())).thenReturn(resultado);

        assertSame(resultado, handler.handler(query));
        verify(application).listar(" Maria ", false, 2, 10);
    }
}
