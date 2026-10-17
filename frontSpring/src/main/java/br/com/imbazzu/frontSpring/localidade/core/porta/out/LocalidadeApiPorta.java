package br.com.imbazzu.frontSpring.localidade.core.porta.out;

import br.com.imbazzu.frontSpring.controller.dto.ResultadoPaginado;
import br.com.imbazzu.frontSpring.localidade.core.dto.CidadeArquivadoResponseDto;
import br.com.imbazzu.frontSpring.localidade.core.dto.CidadeRequestDto;
import br.com.imbazzu.frontSpring.localidade.core.dto.CidadeResponseDto;
import br.com.imbazzu.frontSpring.localidade.core.dto.DestinoRequestDto;
import br.com.imbazzu.frontSpring.localidade.core.dto.DestinoResponseDto;

import java.util.UUID;

public interface LocalidadeApiPorta {

    void salvarCidade(CidadeRequestDto cidade);

    void editarCidade(UUID id, CidadeRequestDto cidade);

    void arquivarCidade(UUID id);

    void desarquivarCidade(UUID id);

    void deletarCidade(UUID id);

    ResultadoPaginado<CidadeResponseDto> listarCidades(String nome, int pagina, int tamanho);

    ResultadoPaginado<CidadeArquivadoResponseDto> listarCidadesArquivadas(String nome, int pagina, int tamanho);

    CidadeResponseDto buscarCidade(UUID id);

    CidadeArquivadoResponseDto buscarCidadeArquivada(UUID id);

    void salvarDestino(DestinoRequestDto destino);

    void editarDestino(UUID id, String nome);

    void arquivarDestino(UUID id);

    void desarquivarDestino(UUID id);

    ResultadoPaginado<DestinoResponseDto> listarDestinos(UUID idCidade, String nome, int pagina, int tamanho);

    ResultadoPaginado<DestinoResponseDto> listarDestinosArquivados(UUID idCidade, String nome, int pagina, int tamanho);

    DestinoResponseDto buscarDestino(UUID id);

    DestinoResponseDto buscarDestinoArquivado(UUID id);
}
