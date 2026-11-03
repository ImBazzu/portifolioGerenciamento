package br.com.imbazzu.secretariaTransporte.agendamento.core.service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

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
        return motivosNaoAplicacao(regra, pessoa, destino, horario).isEmpty();
    }

    public static List<String> motivosNaoAplicacao(
            RegraCadastroAgendaDados regra, PessoaAgendaDados pessoa,
            DestinoAgendaDados destino, LocalTime horario) {
        var motivos = new ArrayList<String>();
        if (!regra.prioridades().contains(pessoa.prioridade().name())) {
            motivos.add("prioridade da pessoa (%s) não está entre as aceitas: %s"
                    .formatted(pessoa.prioridade(), regra.prioridades()));
        }
        if (!regra.idsCidades().isEmpty() && !regra.idsCidades().contains(destino.idCidade())) {
            motivos.add("cidade do destino (%s) não está entre as atendidas: %s"
                    .formatted(destino.idCidade(), regra.idsCidades()));
        }
        if (!horarioPermitido(regra, horario)) {
            var intervalo = regra.inicioHorario() == null
                    ? "até %s".formatted(regra.finalHorario())
                    : regra.finalHorario() == null
                            ? "a partir de %s".formatted(regra.inicioHorario())
                            : "de %s até %s".formatted(regra.inicioHorario(), regra.finalHorario());
            motivos.add("horário %s fora do intervalo permitido (%s)".formatted(horario, intervalo));
        }
        return List.copyOf(motivos);
    }

    private static boolean horarioPermitido(RegraCadastroAgendaDados regra, LocalTime horario) {
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
