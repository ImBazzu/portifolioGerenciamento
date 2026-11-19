package br.com.imbazzu.secretariaTransporte.agendamento.core.model;

import java.util.UUID;

public record PessoaAgendaDados(UUID prioridade, boolean ativa, boolean prioridadeAtiva,
                               boolean buscarNaResidencia, int acompanhantes) {
}
