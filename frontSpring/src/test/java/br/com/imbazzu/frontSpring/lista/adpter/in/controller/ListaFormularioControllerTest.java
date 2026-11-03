package br.com.imbazzu.frontSpring.lista.adpter.in.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import br.com.imbazzu.frontSpring.localidade.core.porta.in.LocalidadeApplicationPorta;
import br.com.imbazzu.frontSpring.controller.dto.ResultadoPaginado;
import br.com.imbazzu.frontSpring.lista.core.dto.ListaRegrasRequestDto;
import br.com.imbazzu.frontSpring.lista.core.dto.ListaRequestDto;
import br.com.imbazzu.frontSpring.lista.core.porta.in.ListaApplicationPorta;
import br.com.imbazzu.frontSpring.veiculo.core.dto.TipoVeiculoResponseDto;
import br.com.imbazzu.frontSpring.veiculo.core.porta.in.VeiculoApplicationPorta;
import org.springframework.ui.ExtendedModelMap;

class ListaFormularioControllerTest {

    @Test
    void formularioOfereceSomenteTiposCarroParaRegrasDeSeparacao() {
        var lista = mock(ListaApplicationPorta.class);
        var localidade = mock(LocalidadeApplicationPorta.class);
        var veiculos = mock(VeiculoApplicationPorta.class);
        var idCarro = UUID.randomUUID();
        var idCarroAdaptado = UUID.randomUUID();
        var idVan = UUID.randomUUID();
        when(veiculos.listar("", 0)).thenReturn(new ResultadoPaginado<>(
                List.of(
                        new TipoVeiculoResponseDto(idCarro, "Carro", 4),
                        new TipoVeiculoResponseDto(idCarroAdaptado, "Carro adaptado", 4),
                        new TipoVeiculoResponseDto(idVan, "Van", 15)),
                0, 20, 3, 1));
        when(localidade.listarCidades("", 0)).thenReturn(new ResultadoPaginado<>(List.of(), 0, 20, 0, 0));
        var controller = new ListaFormularioController(lista, localidade, veiculos);
        var model = new ExtendedModelMap();

        controller.formNovo(model);

        var opcoesSeparacao = model.getAttribute("opcoesVeiculoSeparacao");
        assertTrue(opcoesSeparacao instanceof List<?>);
        assertEquals(List.of(
                new TipoVeiculoResponseDto(idCarro, "Carro", 4),
                new TipoVeiculoResponseDto(idCarroAdaptado, "Carro adaptado", 4)), opcoesSeparacao);
        assertEquals(3, ((List<?>) model.getAttribute("opcoesVeiculo")).size());
    }

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
