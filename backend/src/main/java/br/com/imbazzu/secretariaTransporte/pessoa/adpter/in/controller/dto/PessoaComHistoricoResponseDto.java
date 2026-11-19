package br.com.imbazzu.secretariaTransporte.pessoa.adpter.in.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import br.com.imbazzu.secretariaTransporte.compartilhados.domain.pagination.ResultadoPaginado;
import br.com.imbazzu.secretariaTransporte.agendamento.adpter.in.controller.dto.HistoricoViagemResponseDto;

public record PessoaComHistoricoResponseDto(
        @JsonProperty("pessoa")
        PessoaDetalhesResponseDto pessoa,
        @JsonProperty("historicoViagens")
        ResultadoPaginado<HistoricoViagemResponseDto> historicoViagens) {
}
