package br.com.imbazzu.secretariaTransporte.agendamento.application.command.cancelar;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.imbazzu.secretariaTransporte.agendamento.core.porta.out.AgendamentoCommandPorta;
import br.com.imbazzu.secretariaTransporte.compartilhados.excecoes.gerais.EntidadeNaoEncontradoException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AgendamentoCommandCancelarHandler {

    private final AgendamentoCommandPorta comandos;

    @Transactional
    public void handler(AgendamentoCommandCancelar command) {
        String motivo = command.motivo() == null || command.motivo().isBlank()
                ? null
                : command.motivo().trim();

        if (!comandos.cancelar(command.id(), LocalDateTime.now(), motivo, command.autorId())) {
            throw new EntidadeNaoEncontradoException(
                    "Agendamento não encontrado ou já cancelado: " + command.id());
        }
    }
}
