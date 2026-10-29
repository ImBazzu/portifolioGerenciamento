package br.com.imbazzu.secretariaTransporte.pessoa.application.comand.editar;

import java.util.UUID;

import br.com.imbazzu.secretariaTransporte.pessoa.core.domain.EnumPrioridade;


public record  PessoaCommandEditar(UUID id, String nome, String observacao, EnumPrioridade prioridade,
            String endereco, String cpf, String telefone) {

}
