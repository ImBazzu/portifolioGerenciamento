package br.com.imbazzu.secretariaTransporte.agendamento.adpter.out.repositorio.projecoes;

import br.com.imbazzu.secretariaTransporte.pessoa.core.domain.EnumPrioridade;

public interface PessoaAgendaProjecao {

    EnumPrioridade getPrioridade();

    boolean isAtiva();
}
