package br.com.imbazzu.frontSpring.lista.core.porta.in;

import br.com.imbazzu.frontSpring.controller.dto.ResultadoPaginado;
import br.com.imbazzu.frontSpring.lista.core.dto.ListaRequestDto;
import br.com.imbazzu.frontSpring.lista.core.dto.ListaRegrasRequestDto;
import br.com.imbazzu.frontSpring.lista.core.dto.ListaResponseDto;

import java.util.UUID;

public interface ListaApplicationPorta {

    UUID salvar(ListaRequestDto dto);

    void editar(UUID id, ListaRequestDto dto);

    void atualizarRegras(UUID id, ListaRegrasRequestDto dto);

    void arquivar(UUID id);

    void desarquivar(UUID id);

    void deletar(UUID id);

    ResultadoPaginado<ListaResponseDto> listarAtivas(String nome, int pagina);

    ResultadoPaginado<ListaResponseDto> listarArquivadas(String nome, int pagina);

    ListaResponseDto buscarAtiva(UUID id);

    ListaResponseDto buscarArquivada(UUID id);
}
