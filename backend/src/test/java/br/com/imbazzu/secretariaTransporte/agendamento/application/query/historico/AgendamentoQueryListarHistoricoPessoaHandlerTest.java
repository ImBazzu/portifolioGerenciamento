package br.com.imbazzu.secretariaTransporte.agendamento.application.query.historico;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import br.com.imbazzu.secretariaTransporte.agendamento.adpter.in.controller.dto.HistoricoViagemResponseDto;
import br.com.imbazzu.secretariaTransporte.agendamento.adpter.out.repositorio.AgendamentoQueryRepository;
import br.com.imbazzu.secretariaTransporte.agendamento.adpter.out.repositorio.projecoes.HistoricoViagemProjecao;
import br.com.imbazzu.secretariaTransporte.compartilhados.domain.pagination.ResultadoPaginado;

@ExtendWith(MockitoExtension.class)
class AgendamentoQueryListarHistoricoPessoaHandlerTest {

    @Mock
    private AgendamentoQueryRepository agendamentoQueryRepository;

    @InjectMocks
    private AgendamentoQueryListarHistoricoPessoaHandler handler;

    private UUID idPessoa;
    private HistoricoViagemProjecao projecaoMock;

    @BeforeEach
    void setUp() {
        idPessoa = UUID.randomUUID();
        
        projecaoMock = new HistoricoViagemProjecao() {
            @Override
            public UUID getId() {
                return UUID.randomUUID();
            }

            @Override
            public LocalDate getData() {
                return LocalDate.now();
            }

            @Override
            public LocalTime getHorarioChegada() {
                return LocalTime.of(14, 30);
            }

            @Override
            public long getTempoViagemSegundos() {
                return 3600;
            }

            @Override
            public int getTempoViagemNanos() {
                return 0;
            }

            @Override
            public String getDestinoNome() {
                return "Hospital Central";
            }

            @Override
            public String getListaDescricao() {
                return "Transporte Especial";
            }

            @Override
            public String getTipoVeiculoNome() {
                return "Van Adaptada";
            }

            @Override
            public boolean isBuscarNaResidencia() {
                return true;
            }

            @Override
            public int getAcompanhantes() {
                return 1;
            }
        };
    }

    @Test
    void testBuscarHistoricoSemFiltro() {
        var pageable = PageRequest.of(0, 10);
        var pageResult = new PageImpl<HistoricoViagemProjecao>(java.util.List.of(projecaoMock), pageable, 1);

        when(agendamentoQueryRepository.buscarHistoricoViagensPorPessoa(idPessoa, pageable))
                .thenReturn(pageResult);

        var query = new AgendamentoQueryListarHistoricoPessoa(idPessoa, 0, 10);
        ResultadoPaginado<HistoricoViagemResponseDto> resultado = handler.handler(query);

        assertNotNull(resultado);
        assertEquals(1, resultado.conteudo().size());
        assertEquals(0, resultado.paginaAtual());
        assertEquals(10, resultado.tamanhoPagina());
        assertEquals(1, resultado.totalElementos());
        assertEquals(1, resultado.totalPaginas());
        assertEquals(LocalTime.of(13, 30), resultado.conteudo().getFirst().horarioSaidaNecessario());
    }

    @Test
    void testBuscarHistoricoComFiltro() {
        var pageable = PageRequest.of(0, 10);
        var pageResult = new PageImpl<HistoricoViagemProjecao>(java.util.List.of(projecaoMock), pageable, 1);

        when(agendamentoQueryRepository.buscarHistoricoViagensPorPessoaComBusca(
                eq(idPessoa), eq("Hospital"), eq(pageable)))
                .thenReturn(pageResult);

        var query = new AgendamentoQueryListarHistoricoPessoa(idPessoa, "Hospital", 0, 10);
        ResultadoPaginado<HistoricoViagemResponseDto> resultado = handler.handler(query);

        assertNotNull(resultado);
        assertEquals(1, resultado.conteudo().size());
        assertEquals("Hospital Central", resultado.conteudo().get(0).destinoNome());
    }

    @Test
    void testBuscarHistoricoVazio() {
        var pageable = PageRequest.of(0, 10);
        var pageResult = new PageImpl<HistoricoViagemProjecao>(java.util.List.of(), pageable, 0);

        when(agendamentoQueryRepository.buscarHistoricoViagensPorPessoa(idPessoa, pageable))
                .thenReturn(pageResult);

        var query = new AgendamentoQueryListarHistoricoPessoa(idPessoa, 0, 10);
        ResultadoPaginado<HistoricoViagemResponseDto> resultado = handler.handler(query);

        assertNotNull(resultado);
        assertEquals(0, resultado.conteudo().size());
        assertEquals(0, resultado.totalElementos());
        assertEquals(0, resultado.totalPaginas());
    }

    @Test
    void testPaginacao() {
        var pageable = PageRequest.of(1, 10);
        var pageResult = new PageImpl<HistoricoViagemProjecao>(java.util.List.of(projecaoMock), pageable, 25);

        when(agendamentoQueryRepository.buscarHistoricoViagensPorPessoa(idPessoa, pageable))
                .thenReturn(pageResult);

        var query = new AgendamentoQueryListarHistoricoPessoa(idPessoa, 1, 10);
        ResultadoPaginado<HistoricoViagemResponseDto> resultado = handler.handler(query);

        assertNotNull(resultado);
        assertEquals(1, resultado.paginaAtual());
        assertEquals(25, resultado.totalElementos());
        assertEquals(3, resultado.totalPaginas());
    }
}
