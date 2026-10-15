package br.com.imbazzu.secretariaTransporte.cadastrais.core.porta.in;

import br.com.imbazzu.secretariaTransporte.compartilhados.domain.pagination.ResultadoPaginado;

import java.util.UUID;

public interface PessoaCondicaoApplicationPorta {

    //=====================
    // Command
    //=====================
    UUID salvar(PessoaCondicaoSalvarInputDto dto);

    void editar(UUID id, PessoaCondicaoSalvarInputDto dto);

    void arquivar(UUID id);

    void desarquivar(UUID id);

    //=====================
    // Query
    //=====================
    PessoaCondicaoInfoOutputDto buscarPorId(UUID id);


    ResultadoPaginado<PessoaCondicaoInfoOutputDto> buscarPorNome(String nome, int pagina, int tamanho);


    //=====================
    // Record
    //=====================
    record PessoaCondicaoInfoOutputDto(UUID id, String nome) {
    }

    record PessoaCondicaoSalvarInputDto(String nome) {
    }

}
