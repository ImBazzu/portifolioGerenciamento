package br.com.imbazzu.frontSpring.agendamento.adpter.in.controller.view;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import br.com.imbazzu.frontSpring.pessoa.core.dto.PessoaResponseDto;

class PassageiroVerificadoViewTest {

    @Test
    void incluiEnderecoDaPessoaNaConfirmacaoDoAgendamento() {
        var pessoa = new PessoaResponseDto(UUID.randomUUID(), "Ana Souza", null, UUID.randomUUID(),
                "Padrão", "Alta", UUID.randomUUID(), 2, true, 0, false, null,
                "Rua das Flores, 123", null, "11987654321");

        var view = PassageiroVerificadoView.de(pessoa, null, LocalTime.of(9, 0), true, 1);

        assertEquals("Rua das Flores, 123", view.endereco());
    }
}
