package br.com.imbazzu.frontSpring.agendamento.core.porta.in;

import java.util.UUID;
import br.com.imbazzu.frontSpring.agendamento.core.dto.HistoricoViagemResponseDto;
import br.com.imbazzu.frontSpring.controller.dto.ResultadoPaginado;

public interface HistoricoViagemApplicationPorta {
    ResultadoPaginado<HistoricoViagemResponseDto> buscarHistoricoViagens(UUID idPessoa, int pagina);

    ResultadoPaginado<HistoricoViagemResponseDto> buscarHistoricoViagensComFiltro(
            UUID idPessoa, String destinoBusca, int pagina);
}

