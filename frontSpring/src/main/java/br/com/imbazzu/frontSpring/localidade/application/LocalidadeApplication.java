package br.com.imbazzu.frontSpring.localidade.application;

import br.com.imbazzu.frontSpring.controller.dto.ResultadoPaginado;
import br.com.imbazzu.frontSpring.localidade.core.dto.CidadeArquivadoResponseDto;
import br.com.imbazzu.frontSpring.localidade.core.dto.CidadeRequestDto;
import br.com.imbazzu.frontSpring.localidade.core.dto.CidadeResponseDto;
import br.com.imbazzu.frontSpring.localidade.core.dto.DestinoRequestDto;
import br.com.imbazzu.frontSpring.localidade.core.dto.DestinoResponseDto;
import br.com.imbazzu.frontSpring.localidade.core.porta.in.LocalidadeApplicationPorta;
import br.com.imbazzu.frontSpring.localidade.core.porta.out.LocalidadeApiPorta;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class LocalidadeApplication implements LocalidadeApplicationPorta {

    private static final int TAMANHO_PAGINA = 7;

    private final LocalidadeApiPorta api;

    public LocalidadeApplication(LocalidadeApiPorta api) {
        this.api = api;
    }

    @Override
    public void salvarCidade(CidadeRequestDto cidade) {
        api.salvarCidade(cidade);
    }

    @Override
    public void editarCidade(UUID id, CidadeRequestDto cidade) {
        api.editarCidade(id, cidade);
    }

    @Override
    public void arquivarCidade(UUID id) {
        api.arquivarCidade(id);
    }

    @Override
    public void desarquivarCidade(UUID id) {
        api.desarquivarCidade(id);
    }

    @Override
    public void deletarCidade(UUID id) {
        api.deletarCidade(id);
    }

    @Override
    public ResultadoPaginado<CidadeResponseDto> listarCidades(String nome, int pagina) {
        return api.listarCidades(nome, pagina, TAMANHO_PAGINA);
    }

    @Override
    public ResultadoPaginado<CidadeArquivadoResponseDto> listarCidadesArquivadas(String nome, int pagina) {
        return api.listarCidadesArquivadas(nome, pagina, TAMANHO_PAGINA);
    }

    @Override
    public CidadeResponseDto buscarCidade(UUID id) {
        return api.buscarCidade(id);
    }

    @Override
    public CidadeArquivadoResponseDto buscarCidadeArquivada(UUID id) {
        return api.buscarCidadeArquivada(id);
    }

    @Override
    public void salvarDestino(DestinoRequestDto destino) {
        api.salvarDestino(destino);
    }

    @Override
    public void editarDestino(UUID id, String nome) {
        api.editarDestino(id, nome);
    }

    @Override
    public void arquivarDestino(UUID id) {
        api.arquivarDestino(id);
    }

    @Override
    public void desarquivarDestino(UUID id) {
        api.desarquivarDestino(id);
    }

    @Override
    public ResultadoPaginado<DestinoResponseDto> listarDestinos(UUID idCidade, String nome, int pagina) {
        return api.listarDestinos(idCidade, nome, pagina, TAMANHO_PAGINA);
    }

    @Override
    public ResultadoPaginado<DestinoResponseDto> listarDestinosArquivados(UUID idCidade, String nome, int pagina) {
        return api.listarDestinosArquivados(idCidade, nome, pagina, TAMANHO_PAGINA);
    }

    @Override
    public DestinoResponseDto buscarDestino(UUID id) {
        return api.buscarDestino(id);
    }

    @Override
    public DestinoResponseDto buscarDestinoArquivado(UUID id) {
        return api.buscarDestinoArquivado(id);
    }
}
