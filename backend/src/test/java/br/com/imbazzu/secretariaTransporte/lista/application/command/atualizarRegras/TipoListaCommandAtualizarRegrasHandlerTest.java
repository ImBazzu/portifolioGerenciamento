package br.com.imbazzu.secretariaTransporte.lista.application.command.atualizarRegras;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.RegraDeNegocioException;
import br.com.imbazzu.secretariaTransporte.compartilhados.valueObjects.Nome;
import br.com.imbazzu.secretariaTransporte.lista.adpter.model.RegraSeparacaoInput;
import br.com.imbazzu.secretariaTransporte.lista.core.domain.TipoLista;
import br.com.imbazzu.secretariaTransporte.lista.core.porta.out.TipoListaPorta;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.application.query.model.TipoVeiculoOutput;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.porta.out.TipoVeiculoQueryPorta;

class TipoListaCommandAtualizarRegrasHandlerTest {

    @Test
    void naoPermiteRegraDeSeparacaoParaVan() {
        var listaPorta = mock(TipoListaPorta.class);
        var tipoVeiculoQuery = mock(TipoVeiculoQueryPorta.class);
        var lista = TipoLista.criar(new Nome("Lista teste"), null);
        var idLista = lista.getId();
        var idCarro = UUID.randomUUID();
        var idVan = UUID.randomUUID();
        lista.criarRegraSeparacao(idCarro, Duration.ofMinutes(10));
        when(listaPorta.buscarPorId(idLista)).thenReturn(Optional.of(lista));
        when(tipoVeiculoQuery.buscarAtivo(idVan)).thenReturn(Optional.of(new TipoVeiculoOutput(idVan, "Van", 15)));
        var handler = new TipoListaCommandAtualizarRegrasHandler(listaPorta, tipoVeiculoQuery);
        var command = new TipoListaCommandAtualizarRegras(
                idLista, List.of(), List.of(new RegraSeparacaoInput(idVan, Duration.ofMinutes(5))));

        assertThrows(RegraDeNegocioException.class, () -> handler.handler(command));

        assertEquals(1, lista.getListaRegraSeparacao().size());
        verify(listaPorta, never()).salvar(any());
    }

    @Test
    void permiteRegraDeSeparacaoParaCarro() {
        var listaPorta = mock(TipoListaPorta.class);
        var tipoVeiculoQuery = mock(TipoVeiculoQueryPorta.class);
        var lista = TipoLista.criar(new Nome("Lista teste"), null);
        var idLista = lista.getId();
        var idCarro = UUID.randomUUID();
        when(listaPorta.buscarPorId(idLista)).thenReturn(Optional.of(lista));
        when(tipoVeiculoQuery.buscarAtivo(idCarro)).thenReturn(Optional.of(new TipoVeiculoOutput(idCarro, "Carro", 4)));
        var handler = new TipoListaCommandAtualizarRegrasHandler(listaPorta, tipoVeiculoQuery);

        handler.handler(new TipoListaCommandAtualizarRegras(
                idLista, List.of(), List.of(new RegraSeparacaoInput(idCarro, Duration.ofMinutes(5)))));

        assertEquals(1, lista.getListaRegraSeparacao().size());
        verify(listaPorta).salvar(lista);
    }
}
