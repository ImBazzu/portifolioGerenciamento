package br.com.imbazzu.secretariaTransporte.tipoVeiculo.application.command.deletar;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.OperacaoNaoPermitidaException;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.model.TipoVeiculo;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.model.valueobject.NomeVeiculo;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.porta.out.TipoVeiculoCommandPorta;

@ExtendWith(MockitoExtension.class)
class TipoVeiculoCommandDeletarHandleTest {

    @Mock
    private TipoVeiculoCommandPorta porta;

    @InjectMocks
    private TipoVeiculoCommandDeletarHandle handler;

    @Test
    void naoExcluiTipoAtivo() {
        var id = UUID.randomUUID();
        when(porta.buscarPorId(id)).thenReturn(TipoVeiculo.recriar(
                id, new NomeVeiculo("Carro"), 4, null));

        assertThrows(OperacaoNaoPermitidaException.class,
                () -> handler.handler(new TipoVeiculoCommandDeletar(id)));

        verify(porta).buscarPorId(id);
        verifyNoMoreInteractions(porta);
    }

    @Test
    void excluiTipoArquivado() {
        var id = UUID.randomUUID();
        when(porta.buscarPorId(id)).thenReturn(TipoVeiculo.recriar(
                id, new NomeVeiculo("Carro"), 4, LocalDate.now()));

        handler.handler(new TipoVeiculoCommandDeletar(id));

        verify(porta).deletar(id);
    }
}
