package br.com.imbazzu.frontSpring.lista.application;

import java.util.UUID;

import org.springframework.stereotype.Service;

import br.com.imbazzu.frontSpring.controller.dto.ResultadoPaginado;
import br.com.imbazzu.frontSpring.lista.core.dto.ListaRequestDto;
import br.com.imbazzu.frontSpring.lista.core.dto.ListaRegrasRequestDto;
import br.com.imbazzu.frontSpring.lista.core.dto.ListaResponseDto;
import br.com.imbazzu.frontSpring.lista.core.porta.in.ListaApplicationPorta;
import br.com.imbazzu.frontSpring.lista.core.porta.out.ListaApiPorta;

@Service
public class ListaApplication implements ListaApplicationPorta {

    private static final int TAMANHO_PAGINA = 5;

    private final ListaApiPorta api;

    public ListaApplication(ListaApiPorta api) {
        this.api = api;
    }

    @Override
    public UUID salvar(ListaRequestDto dto) {
        return api.salvar(dto);
    }

    @Override
    public void editar(UUID id, ListaRequestDto dto) {
        api.editar(id, dto);
    }

    @Override
    public void atualizarRegras(UUID id, ListaRegrasRequestDto dto) {
        api.atualizarRegras(id, dto);
    }

    @Override
    public void arquivar(UUID id) {
        api.arquivar(id);
    }

    @Override
    public void desarquivar(UUID id) {
        api.desarquivar(id);
    }

    @Override
    public void deletar(UUID id) {
        api.deletar(id);
    }

    @Override
    public ResultadoPaginado<ListaResponseDto> listarAtivas(String nome, int pagina) {
        return api.listarAtivas(nome, pagina, TAMANHO_PAGINA);
    }

    @Override
    public ResultadoPaginado<ListaResponseDto> listarArquivadas(String nome, int pagina) {
        return api.listarArquivadas(nome, pagina, TAMANHO_PAGINA);
    }

    @Override
    public ListaResponseDto buscarAtiva(UUID id) {
        return api.buscarAtiva(id);
    }

    @Override
    public ListaResponseDto buscarArquivada(UUID id) {
        return api.buscarArquivada(id);
    }
}
