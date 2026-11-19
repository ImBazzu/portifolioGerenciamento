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
import br.com.imbazzu.secretariaTransporte.agendamento.application.query.historico.AgendamentoQueryListarHistoricoPessoaHandler;
import java.util.Optional;
import java.util.UUID;
import br.com.imbazzu.secretariaTransporte.prioridade.PrioridadeJpa;
import br.com.imbazzu.secretariaTransporte.prioridade.PrioridadeRepository;
import br.com.imbazzu.secretariaTransporte.pessoa.core.domain.Pessoa;

class PessoaControllerTest {
    @Test
    void respostaIncluiPrioridadeDaEspecificacao() {
        var buscar = mock(PessoaQueryBuscarHandler.class);
        var prioridades = mock(PrioridadeRepository.class);
        var historicoHandler = mock(AgendamentoQueryListarHistoricoPessoaHandler.class);
        var prioridade = new PrioridadeJpa();
        prioridade.setId(UUID.randomUUID());
        prioridade.setNome("ALTA");
        when(prioridades.findById(prioridade.getId())).thenReturn(Optional.of(prioridade));
        var controller = new PessoaController(null, null, null, null, null, buscar, null, null, null, prioridades, historicoHandler);
        var especificacao = Especificacao.criar("Padrão", prioridade.getId(), null);
        var pessoa = Pessoa.criar(new Nome("Ana Souza"), null, especificacao, true, 2,
                null, null, new Telefone("11987654321"));
        when(buscar.handler(new PessoaQueryBuscar(pessoa.getId()))).thenReturn(pessoa);

        var resposta = controller.buscar(pessoa.getId()).getBody();

        assertEquals("ALTA", resposta.especificacaoPrioridade());
        assertEquals(true, resposta.buscarNaResidencia());
        assertEquals(2, resposta.quantidadeAcompanhantes());
    }
}

