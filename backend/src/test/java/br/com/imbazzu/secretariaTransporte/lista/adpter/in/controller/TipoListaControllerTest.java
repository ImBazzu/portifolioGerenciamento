package br.com.imbazzu.secretariaTransporte.lista.adpter.in.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.imbazzu.secretariaTransporte.compartilhados.valueObjects.Nome;
import br.com.imbazzu.secretariaTransporte.lista.application.TipoListaApplication;
import br.com.imbazzu.secretariaTransporte.lista.core.domain.EnumPrioridade;
import br.com.imbazzu.secretariaTransporte.lista.core.domain.TipoLista;
import br.com.imbazzu.secretariaTransporte.localidades.core.portas.out.CidadeQueryPorta;

@ExtendWith(MockitoExtension.class)
class TipoListaControllerTest {

    @Mock
    private TipoListaApplication application;

    @Mock
    private CidadeQueryPorta cidadeQueryPorta;

    @InjectMocks
    private TipoListaController controller;

    @Test
    void retornaNomesDasCidadesNoDetalheDaLista() {
        var idLista = UUID.randomUUID();
        var idCidadeAtiva = UUID.randomUUID();
        var idCidadeArquivada = UUID.randomUUID();
        var lista = TipoLista.criar(new Nome("Lista teste"), null);
        lista.criarRegraCadastro(
                List.of(UUID.randomUUID()),
                List.of(idCidadeAtiva, idCidadeArquivada),
                List.of(EnumPrioridade.MODERADA),
                null,
                null,
                false,
                null);

        when(application.buscar(idLista)).thenReturn(lista);
        when(cidadeQueryPorta.buscar(idCidadeAtiva)).thenReturn(Optional.of(
                new CidadeQueryPorta.CidadeOutput(idCidadeAtiva, "Cidade ativa", "SP", "0:30", 0)));
        when(cidadeQueryPorta.buscar(idCidadeArquivada)).thenReturn(Optional.empty());
        when(cidadeQueryPorta.buscarArquivado(idCidadeArquivada)).thenReturn(Optional.of(
                new CidadeQueryPorta.CidadeOutputArquivado(
                        idCidadeArquivada, "Cidade arquivada", "SP", null, 0, null)));

        var response = controller.buscar(idLista).getBody();

        assertEquals(
                List.of("Cidade ativa", "Cidade arquivada"),
                response.regrasCadastro().getFirst().cidades());
    }
}
