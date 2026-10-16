package br.com.imbazzu.frontSpring.veiculo.core.porta.in;

import br.com.imbazzu.frontSpring.controller.dto.ResultadoPaginado;
import br.com.imbazzu.frontSpring.veiculo.core.dto.TipoVeiculoArquivadoResponseDto;
import br.com.imbazzu.frontSpring.veiculo.core.dto.TipoVeiculoHistoricoResponseDto;
import br.com.imbazzu.frontSpring.veiculo.core.dto.TipoVeiculoRequestDto;
import br.com.imbazzu.frontSpring.veiculo.core.dto.TipoVeiculoResponseDto;

import java.util.List;
import java.util.UUID;

public interface VeiculoApplicationPorta {

    //
    //Command
    //
    void arquivar(UUID id);

    void desarquivar(UUID id);

    void editar(UUID id, TipoVeiculoRequestDto dto);

    void salvar(TipoVeiculoRequestDto dto);

    void deletar(UUID id);

    //
    //Query
    //

    ResultadoPaginado<TipoVeiculoResponseDto> listar(String nome, int pagina);

    ResultadoPaginado<TipoVeiculoArquivadoResponseDto> listarArquivados(String nome, int pagina);

    TipoVeiculoResponseDto buscar(UUID id);

    TipoVeiculoArquivadoResponseDto buscarArquivado(UUID id);

    List<TipoVeiculoHistoricoResponseDto> buscarHistorico(UUID id);
}
