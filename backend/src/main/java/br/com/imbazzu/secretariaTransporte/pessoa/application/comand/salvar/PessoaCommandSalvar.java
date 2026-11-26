package br.com.imbazzu.secretariaTransporte.pessoa.application.comand.salvar;

import java.util.UUID;

public record PessoaCommandSalvar(String nome, String observacao, UUID especificacaoId,
            String endereco, String cpf, String telefone, boolean buscarNaResidencia, UUID autorId) {
    
}
