package br.com.imbazzu.secretariaTransporte.tipoVeiculo.application.query.listarArquivados;

import org.springframework.stereotype.Service;

import br.com.imbazzu.secretariaTransporte.compartilhados.domain.pagination.ResultadoPaginado;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.application.query.model.TipoVeiculoArquivadoOutput;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.porta.out.TipoVeiculoQueryPorta;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TipoVeiculoQueryListarArquivadoHandler {

    private final TipoVeiculoQueryPorta queryPorta;

    public ResultadoPaginado<TipoVeiculoArquivadoOutput> handler(TipoVeiculoQueryListarArquivado dto) {
        return queryPorta.listarArquivado(dto.filtro(), dto.pagina(), dto.tamanho());
    }
}
