package br.com.imbazzu.frontSpring.agendamento.core.aplicacao;

import java.util.UUID;

import org.springframework.stereotype.Service;

import br.com.imbazzu.frontSpring.agendamento.adpter.out.api.AgendamentoApi;
import br.com.imbazzu.frontSpring.agendamento.core.dto.HistoricoViagemResponseDto;
import br.com.imbazzu.frontSpring.agendamento.core.porta.in.HistoricoViagemApplicationPorta;
import br.com.imbazzu.frontSpring.controller.dto.ResultadoPaginado;

@Service
public class HistoricoViagemApplicationService implements HistoricoViagemApplicationPorta {

    private final AgendamentoApi api;

    public HistoricoViagemApplicationService(AgendamentoApi api) {
        this.api = api;
    }

    @Override
    public ResultadoPaginado<HistoricoViagemResponseDto> buscarHistoricoViagens(UUID idPessoa, int pagina) {
        return api.buscarHistoricoViagens(idPessoa, pagina);
    }

    @Override
    public ResultadoPaginado<HistoricoViagemResponseDto> buscarHistoricoViagensComFiltro(
            UUID idPessoa, String destinoBusca, int pagina) {
        return api.buscarHistoricoViagensComFiltro(idPessoa, destinoBusca, pagina);
    }
}

