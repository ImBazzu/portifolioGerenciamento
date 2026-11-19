package br.com.imbazzu.secretariaTransporte.agendamento.application.query.historico;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.imbazzu.secretariaTransporte.agendamento.adpter.in.controller.dto.HistoricoViagemResponseDto;
import br.com.imbazzu.secretariaTransporte.agendamento.adpter.out.repositorio.AgendamentoQueryRepository;
import br.com.imbazzu.secretariaTransporte.compartilhados.domain.pagination.ResultadoPaginado;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AgendamentoQueryListarHistoricoPessoaHandler {

    private final AgendamentoQueryRepository agendamentoQueryRepository;

    public ResultadoPaginado<HistoricoViagemResponseDto> handler(
            AgendamentoQueryListarHistoricoPessoa query) {
        
        Pageable pageable = PageRequest.of(query.pagina(), query.tamanho());
        
        var resultado = query.destinoBusca() != null && !query.destinoBusca().isBlank()
                ? agendamentoQueryRepository.buscarHistoricoViagensPorPessoaComBusca(
                        query.idPessoa(), query.destinoBusca(), pageable)
                : agendamentoQueryRepository.buscarHistoricoViagensPorPessoa(
                        query.idPessoa(), pageable);
        
        return new ResultadoPaginado<>(
                resultado.getContent().stream()
                        .map(this::toResponse)
                        .toList(),
                query.pagina(),
                query.tamanho(),
                resultado.getTotalElements(),
                resultado.getTotalPages()
        );
    }

    private HistoricoViagemResponseDto toResponse(
            br.com.imbazzu.secretariaTransporte.agendamento.adpter.out.repositorio.projecoes.HistoricoViagemProjecao projecao) {
        return new HistoricoViagemResponseDto(
                projecao.getId(),
                projecao.getData(),
                projecao.getHorarioChegada(),
                projecao.getHorarioSaidaNecessario(),
                projecao.getDestinoNome(),
                projecao.getListaDescricao(),
                projecao.getTipoVeiculoNome(),
                projecao.isBuscarNaResidencia(),
                projecao.getAcompanhantes()
        );
    }
}
