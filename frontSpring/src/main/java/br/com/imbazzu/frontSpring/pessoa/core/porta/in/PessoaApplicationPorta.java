package br.com.imbazzu.frontSpring.pessoa.core.porta.in;

import java.util.UUID;

import br.com.imbazzu.frontSpring.controller.dto.ResultadoPaginado;
import br.com.imbazzu.frontSpring.pessoa.core.dto.PessoaRequestDto;
import br.com.imbazzu.frontSpring.pessoa.core.dto.PessoaResponseDto;

public interface PessoaApplicationPorta {
    ResultadoPaginado<PessoaResponseDto> listar(String nome, boolean arquivadas, int pagina);

    PessoaResponseDto buscar(UUID id);

    void salvar(PessoaRequestDto pessoa);

    void editar(UUID id, PessoaRequestDto pessoa);

    void arquivar(UUID id);

    void desarquivar(UUID id);
}
