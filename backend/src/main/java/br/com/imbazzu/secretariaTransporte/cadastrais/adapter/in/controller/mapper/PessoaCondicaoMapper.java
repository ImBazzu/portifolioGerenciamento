package br.com.imbazzu.secretariaTransporte.cadastrais.adapter.in.controller.mapper;

import br.com.imbazzu.secretariaTransporte.cadastrais.adapter.in.controller.dto.PessoaCondicaoRequestDto;
import br.com.imbazzu.secretariaTransporte.cadastrais.adapter.in.controller.dto.PessoaCondicaoResponseDto;
import br.com.imbazzu.secretariaTransporte.cadastrais.core.porta.in.PessoaCondicaoApplicationPorta;

public class PessoaCondicaoMapper {

    public static PessoaCondicaoResponseDto converterDto(PessoaCondicaoApplicationPorta.PessoaCondicaoInfoOutputDto dto){
        return new PessoaCondicaoResponseDto(dto.id(),dto.nome());
    }

    public static PessoaCondicaoApplicationPorta.PessoaCondicaoSalvarInputDto paraInputSalvar(PessoaCondicaoRequestDto dto) {
            return new PessoaCondicaoApplicationPorta.PessoaCondicaoSalvarInputDto(dto.nome());
        }
}
