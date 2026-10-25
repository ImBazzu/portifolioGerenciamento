package br.com.imbazzu.frontSpring.pessoa.application;

import java.util.UUID;

import org.springframework.stereotype.Service;

import br.com.imbazzu.frontSpring.controller.dto.ResultadoPaginado;
import br.com.imbazzu.frontSpring.pessoa.core.dto.PessoaRequestDto;
import br.com.imbazzu.frontSpring.pessoa.core.dto.PessoaResponseDto;
import br.com.imbazzu.frontSpring.pessoa.core.porta.in.PessoaApplicationPorta;
import br.com.imbazzu.frontSpring.pessoa.core.porta.out.PessoaApiPorta;

@Service
public class PessoaApplication implements PessoaApplicationPorta {
    private static final int TAMANHO_PAGINA = 10;

    private final PessoaApiPorta api;

    public PessoaApplication(PessoaApiPorta api) {
        this.api = api;
    }

    @Override
    public ResultadoPaginado<PessoaResponseDto> listar(String nome, boolean arquivadas, int pagina) {
        return api.listar(nome, arquivadas, pagina, TAMANHO_PAGINA);
    }

    @Override
    public PessoaResponseDto buscar(UUID id) {
        return api.buscar(id);
    }

    @Override
    public void salvar(PessoaRequestDto pessoa) {
        api.salvar(pessoa);
    }

    @Override
    public void editar(UUID id, PessoaRequestDto pessoa) {
        api.editar(id, pessoa);
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
