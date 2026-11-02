package br.com.imbazzu.secretariaTransporte.agendamento.core.service;

import java.time.LocalTime;

import br.com.imbazzu.secretariaTransporte.agendamento.core.model.DestinoAgendaDados;
import br.com.imbazzu.secretariaTransporte.agendamento.core.model.PessoaAgendaDados;
import br.com.imbazzu.secretariaTransporte.agendamento.core.model.RegraCadastroAgendaDados;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.OperacaoNaoPermitidaException;

public final class RegraCadastroAgendamento {

    private RegraCadastroAgendamento() {
    }

    public static boolean aplica(
            RegraCadastroAgendaDados regra, PessoaAgendaDados pessoa,
            DestinoAgendaDados destino, LocalTime horario) {
        if (!regra.prioridades().contains(pessoa.prioridade().name())) {
            return false;
        }
        if (!regra.idsCidades().isEmpty() && !regra.idsCidades().contains(destino.idCidade())) {
            return false;
        }
        if (regra.inicioHorario() == null && regra.finalHorario() == null) {
            return true;
        }
        if (regra.inicioHorario() == null) {
            return !horario.isAfter(regra.finalHorario());
        }
        if (regra.finalHorario() == null) {
            return !horario.isBefore(regra.inicioHorario());
        }
        if (regra.inicioHorario().compareTo(regra.finalHorario()) <= 0) {
            return !horario.isBefore(regra.inicioHorario()) && !horario.isAfter(regra.finalHorario());
        }
        return !horario.isBefore(regra.inicioHorario()) || !horario.isAfter(regra.finalHorario());
    }

    public static void validar(
            RegraCadastroAgendaDados regra, PessoaAgendaDados pessoa,
            DestinoAgendaDados destino, LocalTime horarioChegada, java.util.UUID idTipoVeiculo) {
        if (regra.obrigacaoAnexo()) {
            throw new OperacaoNaoPermitidaException(
                    "A regra exige anexo, mas o agendamento ainda não aceita anexos");
        }
        if (!regra.idsVeiculos().contains(idTipoVeiculo)
                || !aplica(regra, pessoa, destino, horarioChegada)) {
            throw new OperacaoNaoPermitidaException(
                    "A regra selecionada não aprova este passageiro, horário, cidade ou tipo de veículo");
        }
    }
}
