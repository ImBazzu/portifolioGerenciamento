package br.com.imbazzu.frontSpring.pessoa.adpter.in.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.imbazzu.frontSpring.controller.dto.ResultadoPaginado;
import br.com.imbazzu.frontSpring.especificacao.core.porta.in.EspecificacaoApplicationPorta;
import br.com.imbazzu.frontSpring.pessoa.core.dto.EnumPrioridade;
import br.com.imbazzu.frontSpring.pessoa.core.dto.PessoaResponseDto;
import br.com.imbazzu.frontSpring.pessoa.core.porta.in.PessoaApplicationPorta;

class PessoaControllerTest {

    @Test
    void carregaDetalhesDaPessoaParaModal() {
        var application = mock(PessoaApplicationPorta.class);
        var especificacaoApplication = mock(EspecificacaoApplicationPorta.class);
        var controller = new PessoaController(application, especificacaoApplication, new ObjectMapper());
        var id = UUID.randomUUID();
        var especId = UUID.randomUUID();
        var pessoa = new PessoaResponseDto(
                id, "Maria da Silva", "Observação", especId, "Especificação 1", EnumPrioridade.ALTA, true, 2, null,
                "Rua Central, 10", "12345678900", "77999999999");
        var model = new ExtendedModelMap();
        when(application.buscar(id)).thenReturn(pessoa);

        var view = controller.detalhes(id, model);

        assertEquals("paginas/pessoa/detalhesPessoa :: detalhesPessoa", view);
        assertSame(pessoa, model.getAttribute("pessoa"));
        verify(application).buscar(id);
    }

    @Test
    void arquivaPessoaEFechaModalComFragmentoVazio() {
        var application = mock(PessoaApplicationPorta.class);
        var especificacaoApplication = mock(EspecificacaoApplicationPorta.class);
        var controller = new PessoaController(application, especificacaoApplication, new ObjectMapper());
        var id = UUID.randomUUID();

        var view = controller.arquivar(id);

        assertEquals("layout/vazio :: vazio", view);
        verify(application).arquivar(id);
    }
}
