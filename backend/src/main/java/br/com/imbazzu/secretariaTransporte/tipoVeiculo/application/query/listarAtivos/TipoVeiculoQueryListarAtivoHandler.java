package br.com.imbazzu.secretariaTransporte.tipoVeiculo.application.query.listarAtivos;

import org.springframework.stereotype.Service;

import br.com.imbazzu.secretariaTransporte.compartilhados.domain.pagination.ResultadoPaginado;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.application.query.model.TipoVeiculoOutput;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.porta.out.TipoVeiculoQueryPorta;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TipoVeiculoQueryListarAtivoHandler {

    private final TipoVeiculoQueryPorta queryPorta;

    public ResultadoPaginado<TipoVeiculoOutput> handler(TipoVeiculoQueryListarAtivo dto) {
        return queryPorta.listar(dto.filtro(), dto.pagina(), dto.tamanho());
    }
}
