package br.com.imbazzu.secretariaTransporte.especificacao.application.command.salvar;

import br.com.imbazzu.secretariaTransporte.pessoa.core.domain.EnumPrioridade;

public record EspecificacaoCommandSalvar(String nome, EnumPrioridade prioridade, String observacao) {
}
