package br.com.imbazzu.secretariaTransporte.agendamento.application.command.abrirDia;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.imbazzu.secretariaTransporte.agendamento.core.porta.out.AgendamentoCommandPorta;
import br.com.imbazzu.secretariaTransporte.agendamento.core.porta.out.AgendamentoQueryPorta;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.EntidadeDuplicadaException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AgendamentoCommandAbrirDiaHandler {

    private final AgendamentoCommandPorta comandos;
    private final AgendamentoQueryPorta consultas;

    @Transactional
    public void handler(AgendamentoCommandAbrirDia command) {
        if (consultas.diaAberto(command.data())) {
            throw new EntidadeDuplicadaException("O dia já foi aberto: " + command.data());
        }
        comandos.abrirDia(command.data());
    }
}
