package br.com.imbazzu.frontSpring.pessoa.core.dto;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import jakarta.validation.Validation;

class PessoaRequestDtoTest {

    @Test
    void aceitaEnderecoEmBranco() {
        var pessoa = new PessoaRequestDto(
                "Ana Souza", "", UUID.randomUUID(), false, 0, "", "52998224725", "11987654321");
        var validator = Validation.buildDefaultValidatorFactory().getValidator();

        assertTrue(validator.validate(pessoa).isEmpty());
    }
}
