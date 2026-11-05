package br.com.imbazzu.secretariaTransporte.veiculo.application.query.historico;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.imbazzu.secretariaTransporte.compartilhados.domain.pagination.ResultadoPaginado;
import br.com.imbazzu.secretariaTransporte.veiculo.application.query.model.VeiculoHistoricoOutput;
import br.com.imbazzu.secretariaTransporte.veiculo.core.porta.out.VeiculoQueryPorta;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TipoVeiculoQueryHistoricoHandler {

    private final VeiculoQueryPorta queryPorta;

    @Transactional(readOnly = true)
    public ResultadoPaginado<VeiculoHistoricoOutput> handler(TipoVeiculoQueryHistorico query) {
        return queryPorta.buscarHistorico(query.idTipoVeiculo(), query.pagina(), query.tamanho());
    }
}
