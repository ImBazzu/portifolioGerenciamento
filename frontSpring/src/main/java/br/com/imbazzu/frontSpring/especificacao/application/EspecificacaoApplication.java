package br.com.imbazzu.frontSpring.especificacao.application;

import java.util.UUID;

import org.springframework.stereotype.Service;

import br.com.imbazzu.frontSpring.controller.dto.ResultadoPaginado;
import br.com.imbazzu.frontSpring.especificacao.core.dto.EspecificacaoRequestDto;
import br.com.imbazzu.frontSpring.especificacao.core.dto.EspecificacaoResponseDto;
import br.com.imbazzu.frontSpring.especificacao.core.porta.in.EspecificacaoApplicationPorta;
import br.com.imbazzu.frontSpring.especificacao.core.porta.out.EspecificacaoApiPorta;

@Service
public class EspecificacaoApplication implements EspecificacaoApplicationPorta {
    private static final int TAMANHO_PAGINA = 10;

    private final EspecificacaoApiPorta api;

    public EspecificacaoApplication(EspecificacaoApiPorta api) {
        this.api = api;
    }

    @Override
    public ResultadoPaginado<EspecificacaoResponseDto> listar(String nome, boolean arquivadas, int pagina) {
        return api.listar(nome, arquivadas, pagina, TAMANHO_PAGINA);
    }

    @Override
    public EspecificacaoResponseDto buscar(UUID id) {
        return api.buscar(id);
    }

    @Override
    public EspecificacaoResponseDto buscarArquivado(UUID id) {
        return api.buscarArquivado(id);
    }

    @Override
    public UUID salvar(EspecificacaoRequestDto especificacao) {
        return api.salvar(especificacao);
    }

    @Override
    public void editar(UUID id, EspecificacaoRequestDto especificacao) {
        api.editar(id, especificacao);
    }

    @Override
    public void arquivar(UUID id) {
        api.arquivar(id);
    }

    @Override
    public void desarquivar(UUID id) {
        api.desarquivar(id);
    }
}
