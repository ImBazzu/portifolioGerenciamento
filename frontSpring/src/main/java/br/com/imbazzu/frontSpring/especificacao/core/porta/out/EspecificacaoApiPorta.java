package br.com.imbazzu.frontSpring.especificacao.core.porta.out;

import java.util.UUID;

import br.com.imbazzu.frontSpring.controller.dto.ResultadoPaginado;
import br.com.imbazzu.frontSpring.especificacao.core.dto.EspecificacaoRequestDto;
import br.com.imbazzu.frontSpring.especificacao.core.dto.EspecificacaoResponseDto;

public interface EspecificacaoApiPorta {
    ResultadoPaginado<EspecificacaoResponseDto> listar(String nome, boolean arquivadas, int pagina, int tamanho);

    EspecificacaoResponseDto buscar(UUID id);

    EspecificacaoResponseDto buscarArquivado(UUID id);

    UUID salvar(EspecificacaoRequestDto especificacao);

    void editar(UUID id, EspecificacaoRequestDto especificacao);

    void arquivar(UUID id);

    void desarquivar(UUID id);

    void excluir(UUID id);
}
