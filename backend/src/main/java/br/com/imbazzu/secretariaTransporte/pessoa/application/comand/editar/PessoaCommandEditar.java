package br.com.imbazzu.secretariaTransporte.pessoa.application.comand.editar;

import java.util.UUID;

public record  PessoaCommandEditar(UUID id, String nome, String observacao, UUID especificacaoId,
            String endereco, String cpf, String telefone) {

}
