package br.com.imbazzu.frontSpring.lista.core.porta.out;

import java.util.UUID;

import br.com.imbazzu.frontSpring.controller.dto.ResultadoPaginado;
import br.com.imbazzu.frontSpring.lista.core.dto.ListaRequestDto;
import br.com.imbazzu.frontSpring.lista.core.dto.ListaRegrasRequestDto;
import br.com.imbazzu.frontSpring.lista.core.dto.ListaResponseDto;

public interface ListaApiPorta {

    UUID salvar(ListaRequestDto dto);

    void editar(UUID id, ListaRequestDto dto);

    void atualizarRegras(UUID id, ListaRegrasRequestDto dto);

    void arquivar(UUID id);

    void desarquivar(UUID id);

    void deletar(UUID id);

    ResultadoPaginado<ListaResponseDto> listarAtivas(String nome, int pagina, int tamanho);

    ResultadoPaginado<ListaResponseDto> listarArquivadas(String nome, int pagina, int tamanho);

    ListaResponseDto buscarAtiva(UUID id);

    ListaResponseDto buscarArquivada(UUID id);
}
