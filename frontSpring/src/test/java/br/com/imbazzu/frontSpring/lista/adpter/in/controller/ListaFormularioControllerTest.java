package br.com.imbazzu.frontSpring.lista.adpter.in.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import br.com.imbazzu.frontSpring.localidade.core.porta.in.LocalidadeApplicationPorta;
import br.com.imbazzu.frontSpring.lista.core.dto.ListaRegrasRequestDto;
import br.com.imbazzu.frontSpring.lista.core.dto.ListaRequestDto;
import br.com.imbazzu.frontSpring.lista.core.porta.in.ListaApplicationPorta;
import br.com.imbazzu.frontSpring.veiculo.core.porta.in.VeiculoApplicationPorta;

class ListaFormularioControllerTest {

    @Test
    void salvarEncaminhaRegrasDeCadastroESeparacao() throws Exception {
        var application = mock(ListaApplicationPorta.class);
        var idLista = UUID.fromString("b4d0a150-2d80-4f0b-9a1d-3cfb8d8a0f21");
        var idVeiculo = UUID.fromString("d4d0a150-2d80-4f0b-9a1d-3cfb8d8a0f22");
        when(application.salvar(any())).thenReturn(idLista);
        var controller = new ListaFormularioController(
                application,
                mock(LocalidadeApplicationPorta.class),
                mock(VeiculoApplicationPorta.class));

        String regrasJson = """
                {
                  "regrasCadastro": [{
                    "idsVeiculos": ["%s"],
                    "idsCidades": [],
                    "prioridades": ["ALTA"],
                    "inicioHorario": "08:00",
                    "finalHorario": "12:00",
                    "obrigacaoAnexo": true,
                    "descricao": "Documentação obrigatória"
                  }],
                  "regrasSeparacao": [{
                    "idTipoVeiculo": "%s",
                    "tempoToleranciaSegundos": 300,
                    "tempoToleranciaNanos": 0
                  }]
                }
                """.formatted(idVeiculo, idVeiculo);

        assertEquals("layout/vazio :: vazio", controller.salvar("Lista Centro", "", regrasJson));
        verify(application).salvar(new ListaRequestDto("Lista Centro", ""));
        verify(application).atualizarRegras(idLista, new ListaRegrasRequestDto(
                List.of(new ListaRegrasRequestDto.RegraCadastro(
                        List.of(idVeiculo),
                        List.of(),
                        List.of("ALTA"),
                        "08:00",
                        "12:00",
                        true,
                        "Documentação obrigatória")),
                List.of(new ListaRegrasRequestDto.RegraSeparacao(idVeiculo, 300, 0))));
    }
}
