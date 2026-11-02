package br.com.imbazzu.secretariaTransporte.agendamento.core.service;

import java.time.LocalDate;
import java.util.UUID;

import br.com.imbazzu.secretariaTransporte.agendamento.core.domain.Agendamento;
import br.com.imbazzu.secretariaTransporte.agendamento.core.model.DestinoAgendaDados;
import br.com.imbazzu.secretariaTransporte.agendamento.core.model.PessoaAgendaDados;
import br.com.imbazzu.secretariaTransporte.agendamento.core.porta.out.AgendamentoQueryPorta;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.EntidadeNaoEncontradoException;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.OperacaoNaoPermitidaException;

public final class AgendamentoValidacao {

    private AgendamentoValidacao() {
    }

    public static void exigirDiaAberto(AgendamentoQueryPorta consultas, LocalDate data) {
        if (!consultas.diaAberto(data)) {
            throw new EntidadeNaoEncontradoException("A agenda do dia não foi aberta: " + data);
        }
    }

    public static PessoaAgendaDados validarPessoa(AgendamentoQueryPorta consultas, UUID idPessoa) {
        var pessoa = consultas.buscarPessoa(idPessoa);
        if (!pessoa.ativa()) {
            throw new OperacaoNaoPermitidaException("Não é possível agendar uma pessoa arquivada");
        }
        return pessoa;
    }

    public static DestinoAgendaDados validarDestino(AgendamentoQueryPorta consultas, UUID idDestino) {
        var destino = consultas.buscarDestino(idDestino);
        if (!destino.ativo()) {
            throw new OperacaoNaoPermitidaException(
                    "Não é possível agendar para um destino ou cidade arquivados");
        }
        return destino;
    }

    public static void validarLista(AgendamentoQueryPorta consultas, UUID idLista) {
        if (!consultas.listaAtiva(idLista)) {
            throw new OperacaoNaoPermitidaException("A lista não existe ou está arquivada");
        }
    }

    public static Agendamento buscarAgendamento(AgendamentoQueryPorta consultas, UUID id) {
        return consultas.buscarAgendamento(id)
                .orElseThrow(() -> new EntidadeNaoEncontradoException("Agendamento não encontrado: " + id));
    }
}
