package br.com.imbazzu.secretariaTransporte.cadastrais.adapter.in.controller.mapper;

import br.com.imbazzu.secretariaTransporte.cadastrais.adapter.in.controller.dto.PessoaRequestDto;
import br.com.imbazzu.secretariaTransporte.cadastrais.adapter.in.controller.dto.PessoaResponseDto;
import br.com.imbazzu.secretariaTransporte.cadastrais.core.porta.in.PessoaApplicationPorta;

public class PessoaControllerMapper {


    public static PessoaResponseDto toResponse(PessoaApplicationPorta.PessoaInfoOutputDto dto) {

        return new PessoaResponseDto(
                dto.id(),
                dto.nome(),
                dto.cpf(),
                dto.idCondicao(),
                dto.telefone(),
                dto.endereco(),
                dto.observacao());
    }


    public static PessoaApplicationPorta.PessoaSalvarInputDto toInputDto(PessoaRequestDto dto) {
        return new PessoaApplicationPorta.PessoaSalvarInputDto(
                dto.nome(),dto.cpf(),dto.idCondicao(),dto.telefone(),dto.endereco(),dto.observacao()
        );
    }

}
