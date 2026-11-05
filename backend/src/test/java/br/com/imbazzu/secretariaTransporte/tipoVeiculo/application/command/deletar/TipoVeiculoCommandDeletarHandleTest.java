package br.com.imbazzu.secretariaTransporte.tipoVeiculo.application.command.deletar;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.DadosInvalidosException;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.model.TipoVeiculo;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.model.valueobject.NomeVeiculo;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.porta.out.TipoVeiculoCommandPorta;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.porta.out.TipoVeiculoQueryPorta;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.service.TipoVeiculoServiceBuscarEntidade;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.service.TipoVeiculoServiceValidarExclusao;

@ExtendWith(MockitoExtension.class)
class TipoVeiculoCommandDeletarHandleTest {

    @Mock
    private TipoVeiculoCommandPorta porta;

    @Mock
    private TipoVeiculoQueryPorta queryPorta;

    @Test
    void naoExcluiTipoAtivo() {
        var id = UUID.randomUUID();
        when(queryPorta.buscarEntidadePorId(id)).thenReturn(Optional.of(
                TipoVeiculo.recriar(id, new NomeVeiculo("Van"), 15, null)));
        var handler = criarHandler();

        assertThrows(DadosInvalidosException.class,
                () -> handler.handler(new TipoVeiculoCommandDeletar(id)));

        verify(queryPorta).buscarEntidadePorId(id);
        verifyNoMoreInteractions(porta, queryPorta);
    }

    @Test
    void excluiTipoArquivado() {
        var id = UUID.randomUUID();
        when(queryPorta.buscarEntidadePorId(id)).thenReturn(Optional.of(
                TipoVeiculo.recriar(id, new NomeVeiculo("Van"), 15, LocalDate.now())));
        when(queryPorta.existeVeiculoUsandoTipoVeiculo(id)).thenReturn(false);
        var handler = criarHandler();

        handler.handler(new TipoVeiculoCommandDeletar(id));

        verify(queryPorta).buscarEntidadePorId(id);
        verify(queryPorta).existeVeiculoUsandoTipoVeiculo(id);
        verify(porta).deletar(id);
    }

    private TipoVeiculoCommandDeletarHandle criarHandler() {
        return new TipoVeiculoCommandDeletarHandle(
                porta,
                new TipoVeiculoServiceBuscarEntidade(queryPorta),
                new TipoVeiculoServiceValidarExclusao(queryPorta));
    }
}
