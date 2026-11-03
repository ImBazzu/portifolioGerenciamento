package br.com.imbazzu.frontSpring.pessoa.core.dto;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import jakarta.validation.Validation;

class PessoaRequestDtoTest {

    @Test
    void aceitaEnderecoEmBranco() {
        var pessoa = new PessoaRequestDto(
                "Ana Souza", "", PessoaPrioridade.MODERADA, "", "52998224725", "11987654321");
        var validator = Validation.buildDefaultValidatorFactory().getValidator();

        assertTrue(validator.validate(pessoa).isEmpty());
    }
}
