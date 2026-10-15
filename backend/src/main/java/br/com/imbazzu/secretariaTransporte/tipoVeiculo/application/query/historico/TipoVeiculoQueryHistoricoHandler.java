package br.com.imbazzu.secretariaTransporte.tipoVeiculo.application.query.historico;

import org.springframework.stereotype.Service;

import br.com.imbazzu.secretariaTransporte.compartilhados.domain.pagination.ResultadoPaginado;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.application.query.model.TipoVeiculoHistoricoOutput;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.porta.out.TipoVeiculoQueryPorta;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TipoVeiculoQueryHistoricoHandler {

    private final TipoVeiculoQueryPorta queryPorta;

    public ResultadoPaginado<TipoVeiculoHistoricoOutput> handler(TipoVeiculoQueryHistorico dto) {
        return queryPorta.listarHistorico(dto.idVeiculo(), dto.pagina(), dto.tamanho());
    }
}
