package br.com.imbazzu.secretariaTransporte.pessoa.adpter.in.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import br.com.imbazzu.secretariaTransporte.cadastrais.core.model.valueobjects.Telefone;
import br.com.imbazzu.secretariaTransporte.compartilhados.valueObjects.Nome;
import br.com.imbazzu.secretariaTransporte.especificacao.core.domain.Especificacao;
import br.com.imbazzu.secretariaTransporte.pessoa.application.query.buscar.PessoaQueryBuscar;
import br.com.imbazzu.secretariaTransporte.pessoa.application.query.buscar.PessoaQueryBuscarHandler;
import br.com.imbazzu.secretariaTransporte.pessoa.core.domain.EnumPrioridade;
import br.com.imbazzu.secretariaTransporte.pessoa.core.domain.Pessoa;

class PessoaControllerTest {
    @Test
    void respostaIncluiPrioridadeDaEspecificacao() {
        var buscar = mock(PessoaQueryBuscarHandler.class);
        var controller = new PessoaController(null, null, null, null, null, buscar, null, null, null);
        var especificacao = Especificacao.criar("Padrão", EnumPrioridade.ALTA, null);
        var pessoa = Pessoa.criar(new Nome("Ana Souza"), null, especificacao, true, 2,
                null, null, new Telefone("11987654321"));
        when(buscar.handler(new PessoaQueryBuscar(pessoa.getId()))).thenReturn(pessoa);

        var resposta = controller.buscar(pessoa.getId()).getBody();

        assertEquals(EnumPrioridade.ALTA, resposta.especificacaoPrioridade());
        assertEquals(true, resposta.buscarNaResidencia());
        assertEquals(2, resposta.quantidadeAcompanhantes());
    }
}
