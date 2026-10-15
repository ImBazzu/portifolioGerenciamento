package br.com.imbazzu.secretariaTransporte.tipoVeiculo.application.query.buscar;

import org.springframework.stereotype.Service;

import br.com.imbazzu.secretariaTransporte.tipoVeiculo.application.query.model.TipoVeiculoOutput;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.porta.out.TipoVeiculoQueryPorta;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TipoVeiculoQueryBuscarHandler {

    private final TipoVeiculoQueryPorta query;

    public TipoVeiculoOutput handler(TipoVeiculoQueryBuscar dto) {
        return query.buscarAtivo(dto.id());
    }
}
