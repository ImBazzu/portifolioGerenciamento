package br.com.imbazzu.secretariaTransporte.pessoa.application.comand.editar;

import java.util.UUID;

public record  PessoaCommandEditar(UUID id, String nome, String observacao, UUID especificacaoId,
            boolean buscarNaResidencia, int quantidadeAcompanhantes, String endereco, String cpf, String telefone) {

}
