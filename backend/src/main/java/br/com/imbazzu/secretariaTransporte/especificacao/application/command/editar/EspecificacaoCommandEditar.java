package br.com.imbazzu.secretariaTransporte.especificacao.application.command.editar;

import java.util.UUID;


public record EspecificacaoCommandEditar(UUID id, String nome, UUID prioridade, String observacao) {
}
