package br.com.imbazzu.secretariaTransporte.especificacao.application.command.editar;

import java.util.UUID;

import br.com.imbazzu.secretariaTransporte.pessoa.core.domain.EnumPrioridade;

public record EspecificacaoCommandEditar(UUID id, String nome, EnumPrioridade prioridade, String observacao) {
}
