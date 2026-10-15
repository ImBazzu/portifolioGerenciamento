package br.com.imbazzu.secretariaTransporte.tipoVeiculo.application.query.buscarArquivado;

import org.springframework.stereotype.Service;

import br.com.imbazzu.secretariaTransporte.tipoVeiculo.application.query.model.TipoVeiculoArquivadoOutput;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.porta.out.TipoVeiculoQueryPorta;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TipoVeiculoQueryBuscarArquivadoHandler {

    private final TipoVeiculoQueryPorta queryPorta;

    public TipoVeiculoArquivadoOutput handler(TipoVeiculoQueryBuscarArquivado dto) {
        return queryPorta.buscarArquivado(dto.id());
    }
}
