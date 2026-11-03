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

import br.com.imbazzu.frontSpring.pessoa.core.dto.PessoaPrioridade;
import br.com.imbazzu.frontSpring.pessoa.core.dto.PessoaResponseDto;
import br.com.imbazzu.frontSpring.pessoa.core.porta.in.PessoaApplicationPorta;

class PessoaControllerTest {

    @Test
    void carregaDetalhesDaPessoaParaModal() {
        var application = mock(PessoaApplicationPorta.class);
        var controller = new PessoaController(application, new ObjectMapper());
        var id = UUID.randomUUID();
        var pessoa = new PessoaResponseDto(
                id, "Maria da Silva", "Observação", PessoaPrioridade.ALTA, null,
                "Rua Central, 10", "12345678900", "77999999999");
        var model = new ExtendedModelMap();
        when(application.buscar(id)).thenReturn(pessoa);

        var view = controller.detalhes(id, model);

        assertEquals("paginas/pessoa/detalhesPessoa :: detalhesPessoa", view);
        assertSame(pessoa, model.getAttribute("pessoa"));
        verify(application).buscar(id);
    }
}
