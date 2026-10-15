package br.com.imbazzu.frontspring.veiculo.core.porta.out;


import br.com.imbazzu.frontspring.controller.dto.ResultadoPaginado;
import br.com.imbazzu.frontspring.veiculo.core.dto.TipoVeiculoArquivadoResponseDto;
import br.com.imbazzu.frontspring.veiculo.core.dto.TipoVeiculoRequestDto;
import br.com.imbazzu.frontspring.veiculo.core.dto.TipoVeiculoResponseDto;

import java.util.UUID;

public interface VeiculoApiPorta {

    //
    //COMMAND
    //
    void arquivar(UUID id);

    void desarquivar(UUID id);

    void deletar(UUID id);

    void editar(UUID id, TipoVeiculoRequestDto dto );

    void salvar(TipoVeiculoRequestDto dto);

    //
    //Query
    //
    ResultadoPaginado<TipoVeiculoResponseDto> listar(String nome, int pagina, int tamanho);

    ResultadoPaginado<TipoVeiculoArquivadoResponseDto> listarArquivados(String nome, int pagina, int tamanho);

    TipoVeiculoResponseDto buscar(UUID id);

    TipoVeiculoArquivadoResponseDto buscarArquivado(UUID id);

}
