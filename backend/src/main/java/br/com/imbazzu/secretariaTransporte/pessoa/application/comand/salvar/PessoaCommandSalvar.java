package br.com.imbazzu.secretariaTransporte.pessoa.application.comand.salvar;

import br.com.imbazzu.secretariaTransporte.pessoa.core.domain.EnumPrioridade;


public record PessoaCommandSalvar(String nome, String observacao, EnumPrioridade prioridade,
            String endereco, String cpf, String telefone) {
    
}
