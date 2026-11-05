package br.com.imbazzu.secretariaTransporte.tipoVeiculo.application.query.buscar;

import org.springframework.stereotype.Service;

import br.com.imbazzu.secretariaTransporte.tipoVeiculo.application.query.model.TipoVeiculoOutput;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.excecoes.TipoVeiculoNaoEncontradoException;
import br.com.imbazzu.secretariaTransporte.tipoVeiculo.core.porta.out.TipoVeiculoQueryPorta;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TipoVeiculoQueryBuscarHandler {

    private final TipoVeiculoQueryPorta query;

    public TipoVeiculoOutput handler(TipoVeiculoQueryBuscar dto) {
        //Busca o tipo de veículo ativo pelo ID fornecido no DTO.
        // Se não for encontrado, lança uma exceção TipoVeiculoNaoEncontradoException.
        return query.buscarAtivo(dto.id()).orElseThrow(
            ()->new TipoVeiculoNaoEncontradoException(dto.id())
        );
    }
}
