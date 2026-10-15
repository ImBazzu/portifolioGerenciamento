package br.com.imbazzu.secretariaTransporte.cadastrais.application.mapper;

import br.com.imbazzu.secretariaTransporte.cadastrais.core.model.PessoaCondicao;
import br.com.imbazzu.secretariaTransporte.compartilhados.valueObjects.Nome;
import br.com.imbazzu.secretariaTransporte.cadastrais.core.porta.in.PessoaCondicaoApplicationPorta.PessoaCondicaoSalvarInputDto;
import br.com.imbazzu.secretariaTransporte.cadastrais.core.porta.in.PessoaCondicaoApplicationPorta.PessoaCondicaoInfoOutputDto;


public class PessoaCondicaoApplicationMapper {

    public static PessoaCondicao paraDomain(PessoaCondicaoSalvarInputDto dto){
        return PessoaCondicao.criar(new Nome(dto.nome()));
    }

    public static PessoaCondicaoInfoOutputDto paraInfo(PessoaCondicao pessoaCondicao) {
        return new PessoaCondicaoInfoOutputDto(pessoaCondicao.getId(),pessoaCondicao.getNome().valor());
    }

    public static void atualizar(PessoaCondicao pessoaCondicao, PessoaCondicaoSalvarInputDto dto){
        pessoaCondicao.editar(new Nome(dto.nome()));
    }
}
