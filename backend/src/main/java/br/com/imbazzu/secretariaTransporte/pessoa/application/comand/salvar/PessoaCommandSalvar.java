package br.com.imbazzu.secretariaTransporte.pessoa.application.comand.salvar;

import java.util.UUID;

public record PessoaCommandSalvar(String nome, String observacao, UUID especificacaoId,
            boolean buscarNaResidencia, int quantidadeAcompanhantes, String endereco, String cpf, String telefone) {
    
}
