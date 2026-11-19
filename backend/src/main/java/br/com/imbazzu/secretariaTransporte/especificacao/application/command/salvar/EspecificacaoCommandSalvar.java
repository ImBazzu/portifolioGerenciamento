package br.com.imbazzu.secretariaTransporte.especificacao.application.command.salvar;

import java.util.UUID;

public record EspecificacaoCommandSalvar(String nome, UUID prioridade, String observacao) {
}
