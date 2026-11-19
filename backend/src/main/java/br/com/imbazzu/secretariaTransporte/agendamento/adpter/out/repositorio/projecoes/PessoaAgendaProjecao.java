package br.com.imbazzu.secretariaTransporte.agendamento.adpter.out.repositorio.projecoes;

import java.util.UUID;

public interface PessoaAgendaProjecao {

    UUID getPrioridade();

    boolean isBuscarNaResidencia();

    int getAcompanhantes();

    boolean isAtiva();

    boolean isPrioridadeAtiva();
}
