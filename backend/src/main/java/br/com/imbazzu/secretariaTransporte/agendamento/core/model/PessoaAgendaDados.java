package br.com.imbazzu.secretariaTransporte.agendamento.core.model;

import br.com.imbazzu.secretariaTransporte.pessoa.core.domain.EnumPrioridade;

public record PessoaAgendaDados(EnumPrioridade prioridade, boolean ativa) {
}
