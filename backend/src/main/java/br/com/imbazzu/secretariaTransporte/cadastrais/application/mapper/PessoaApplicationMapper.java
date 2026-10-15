package br.com.imbazzu.secretariaTransporte.cadastrais.application.mapper;

import br.com.imbazzu.secretariaTransporte.cadastrais.core.model.Pessoa;
import br.com.imbazzu.secretariaTransporte.cadastrais.core.model.valueobjects.*;
import br.com.imbazzu.secretariaTransporte.cadastrais.core.porta.in.PessoaApplicationPorta.PessoaInfoOutputDto;
import br.com.imbazzu.secretariaTransporte.cadastrais.core.porta.in.PessoaApplicationPorta.PessoaSalvarInputDto;
import br.com.imbazzu.secretariaTransporte.compartilhados.valueObjects.Nome;


public class PessoaApplicationMapper {

    public static Pessoa paraDomain(PessoaSalvarInputDto dto) {
        return Pessoa.criar(new Nome(dto.nome()),new Cpf(dto.cpf()),
                new Telefone(dto.telefone()),dto.idCondicao(),
                new Endereco(dto.endereco()),new Observacao(dto.observacao()));
    }

    public static PessoaInfoOutputDto paraInfo(Pessoa pessoa) {
        return new PessoaInfoOutputDto(pessoa.getId(), pessoa.getNome().valor(),
                pessoa.getCpf().valor(), pessoa.getPessoaCondicaoId(),
                pessoa.getTelefone().valor(),pessoa.getEndereco().valor(),
                pessoa.getObservacao().valor());
    }

    public static void atualizar(Pessoa pessoa, PessoaSalvarInputDto dto){
        pessoa.editar(new Nome(dto.nome()),new Cpf(dto.cpf()),new Telefone(dto.telefone()),
                dto.idCondicao(),new Endereco(dto.endereco()),new Observacao(dto.observacao()));
    }

}
