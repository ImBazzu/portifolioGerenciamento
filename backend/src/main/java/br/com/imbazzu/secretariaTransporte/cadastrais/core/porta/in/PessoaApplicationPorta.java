package br.com.imbazzu.secretariaTransporte.cadastrais.core.porta.in;

import br.com.imbazzu.secretariaTransporte.compartilhados.domain.pagination.ResultadoPaginado;

import java.util.UUID;

public interface PessoaApplicationPorta {

    //=====================
    // Command
    //=====================


    UUID salvar(PessoaSalvarInputDto dto);

    void editar(UUID  id, PessoaSalvarInputDto dto);

    void arquivar(UUID idPessoa);

    void desarquivar(UUID idPessoa);

    //=====================
    // Query
    //=====================

    PessoaInfoOutputDto buscarPorId(UUID id);


    ResultadoPaginado<PessoaInfoOutputDto> listarPorNome(String nome, int pagina, int tamanho);



    //=====================
    // Record
    //=====================


    record PessoaInfoOutputDto(
            UUID id, String nome, String cpf,
            UUID idCondicao, String telefone, String endereco, String observacao) {
    }

    record PessoaSalvarInputDto(String nome, String cpf,UUID idCondicao, String telefone,
                                       String endereco,String observacao ) {
    }

}
